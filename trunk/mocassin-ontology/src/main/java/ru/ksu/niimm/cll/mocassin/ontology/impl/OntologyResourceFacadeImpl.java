package ru.ksu.niimm.cll.mocassin.ontology.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.ontology.ABoxTriple;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OntologyResourceFacadeImpl implements OntologyResourceFacade {
	private static final String AFFILIATION_ELEMENT_KEY = "5";
	private static final String PUBLICATION_TYPE_URI = "http://www.aktors.org/ontology/portal#Article-Reference";
	private static final String RETRIEVED_OBJECT_CLASS = "oclass";
	private static final String RETRIEVED_SUBJECT_CLASS = "sclass";
	private static final String RETRIEVED_OBJECT_GRAPH_NODE = "o";
	private static final String RETRIEVED_PREDICATE_GRAPH_NODE = "p";
	private static final String RETRIEVED_SUBJECT_GRAPH_NODE = "s";
	private static final String RETRIEVED_TITLE_ELEMENT_KEY = "3";
	private static final String DELIMITER = "#";
	private static final String RETRIEVED_AUTHOR_NAME_ELEMENT_KEY = "3";
	private static final String RETRIEVED_LINK_TYPE_ELEMENT_KEY = "3";
	private static final String RETRIEVED_LINK_HREF_ELEMENT_KEY = "4";
	private static final String RETRIEVED_PUBLICATION_URI_ELEMENT_KEY = "1";
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"";
	private static final String RETRIEVED_ARXIV_ID_ELEMENT_KEY = "2";

	private final InsertQueryGenerator insertQueryGenerator;

	private final SparqlQueryLoader sparqlQueryLoader;

	private final OntologyFacade ontologyFacade;

	private final RepositoryProvider<Repository> repositoryProvider;
	@Inject
	private Logger logger;

	private RDFGraph searchGraph;

	private String ontologyRulesSetName;

	@Inject
	public OntologyResourceFacadeImpl(
			InsertQueryGenerator insertQueryGenerator,
			SparqlQueryLoader sparqlQueryLoader, OntologyFacade ontologyFacade,
			RepositoryProvider<Repository> repositoryProvider,
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password,
			@Named("graph.iri") String graphIri,
			@Named("ontology.rules.set") String ontologyRuleSet) {
		this.insertQueryGenerator = insertQueryGenerator;
		this.sparqlQueryLoader = sparqlQueryLoader;
		this.ontologyFacade = ontologyFacade;
		this.repositoryProvider = repositoryProvider;
		this.searchGraph = new RDFGraphImpl.Builder(graphIri)
				.url(connectionUrl).username(username).password(password)
				.inferenceRulesSetName(ontologyRuleSet).build();
		this.ontologyRulesSetName = ontologyRuleSet;
	}

	@Override
	public ArticleMetadata load(OntologyResource resource) {
		/**
		 * TODO: simplify; consider reduce SPARQL queries executed merging
		 * related functionality
		 */
		String resourceUri = resource.getUri();
		boolean isPublication = false;
		try {
			isPublication = isPublication(resourceUri);
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
					"Failed to check if the document='%s' is a publication",
					resourceUri, e.getCause()));
			return null;
		}
		if (isPublication) {
			try {
				return loadPublication(resourceUri);
			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"Failed to load the document='%s' due to %s",
						resourceUri, e.getCause()));
				return null;
			}
		} else {
			return loadSegmentInfo(resourceUri);
		}

	}

	private ArticleMetadata loadSegmentInfo(String resourceUri) {
		try {
			String segmentInfoQueryString = generateSegmentInfoQuery(resourceUri);
			RepositoryConnection connection = getRepository().getConnection();
			TupleQuery segmentTupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, segmentInfoQueryString);
			TupleQueryResult segmentInfoResult = segmentTupleQuery.evaluate();
			String segmentTitle;
			String documentUri;
			int numPage;
			try {
				if (!segmentInfoResult.hasNext()) {
					logger.log(Level.SEVERE,
							"none of a title and publication metadata was found for a segment: "
									+ resourceUri);
					return null;
				}
				BindingSet segmentInfoBindingSet = segmentInfoResult.next();
				segmentTitle = segmentInfoBindingSet.getValue("stitle")
						.stringValue();
				documentUri = segmentInfoBindingSet.getValue("p").stringValue();
				numPage = ((Literal) segmentInfoBindingSet.getValue("snumpage"))
						.intValue();
			} finally {
				segmentInfoResult.close();
				connection.close();
			}

			ArticleMetadata containedPublication = loadPublication(documentUri);
			containedPublication.setCurrentSegmentUri(resourceUri);
			containedPublication.setCurrentSegmentTitle(segmentTitle);
			containedPublication.setCurrentPageNumber(numPage);
			return containedPublication;
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
					"Failed to load info of the segment='%s' due to %s",
					resourceUri, e.getCause()));
			return null;
		}
	}

	@Override
	public List<SGEdge> retrieveStructureGraph(OntologyResource resource) {
		List<SGEdge> edges = new ArrayList<SGEdge>();
		String documentUri = parseDocumentUri(resource.getUri());
		String graphQueryString = generateRetrieveStructureGraphQuery(documentUri);
		try {
			RepositoryConnection connection = getRepository().getConnection();
			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, graphQueryString);
			TupleQueryResult result = tupleQuery.evaluate();
			try {
				if (!result.hasNext())
					return edges;
				final Function<BindingSet, SGEdge> function = new SolutionFunction();
				SGEdge curEdge = function.apply(result.next());

				while (result.hasNext()) {
					SGEdge nextEdge = function.apply(result.next());
					if (nextEdge.equals(curEdge)) {
						MocassinOntologyClasses curSubjectType = curEdge
								.getSubject().getType();
						MocassinOntologyClasses curObjectType = curEdge
								.getObject().getType();
						MocassinOntologyClasses subjectType = nextEdge
								.getSubject().getType();
						MocassinOntologyClasses objectType = nextEdge
								.getObject().getType();
						boolean isMoreSpecificSubject = subjectType
								.equals(this.ontologyFacade.getMoreSpecific(
										curSubjectType, subjectType));
						boolean isMoreSpecificObject = objectType
								.equals(this.ontologyFacade.getMoreSpecific(
										curObjectType, objectType));
						if (isMoreSpecificSubject && isMoreSpecificObject) {
							curEdge = nextEdge;
						}
					} else {
						edges.add(curEdge);
						curEdge = nextEdge;
					}
				}

				if (!edges.contains(curEdge))
					edges.add(curEdge);
			} finally {
				result.close();
				connection.close();
			}
			return edges;
		} catch (Exception e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"Failed to retrieve the structural graph of the document='%s' due to %s",
							resource.getUri(), e.getCause()));
			return null;
		}
	}

	@Override
	public List<ArticleMetadata> loadAll() {
		List<ArticleMetadata> list = new ArrayList<ArticleMetadata>();
		List<String> publicationsUris = retrievePublications();
		for (String uri : publicationsUris) {
			try {
				ArticleMetadata metadata = loadPublication(uri);
				list.add(metadata);
			} catch (Exception e) {
				logger.log(
						Level.SEVERE,
						String.format(
								"Couldn't load metadata of the document='%s' due to %s",
								uri, e.getCause()));
			}
		}
		return list;
	}

	@Override
	public boolean insert(Set<RDFTriple> data) {
		String insertQuery = insertQueryGenerator.generate(new ArrayList(data));
		try {
			RepositoryConnection connection = getRepository().getConnection();
			Update updateQuery = connection.prepareUpdate(QueryLanguage.SPARQL,
					insertQuery);
			updateQuery.execute();
			connection.close();
			return true;
		} catch (Exception e) {
			logger.log(
					Level.SEVERE,
					String.format("Failed to insert triples due to %s",
							e.getCause()));
			return false;
		}
	}

	private ArticleMetadata loadPublication(String documentUri)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String title = retrieveTitle(documentUri);
		// String arxivId = retrieveArxivId(documentUri); // TODO: workaround
		// for Mathnet keys
		String mathnetKey = StringUtil.extractMathnetKeyFromURI(documentUri);
		List<Author> authors = retrieveAuthors(documentUri);
		List<Link> links = retrieveLinks(documentUri);

		ArticleMetadata articleMetadata = new ArticleMetadata();
		articleMetadata.setCollectionId(mathnetKey);
		articleMetadata.setId(documentUri);
		articleMetadata.setTitle(title);
		articleMetadata.setAuthors(authors);
		articleMetadata.setLinks(links);
		return articleMetadata;
	}

	private List<String> retrievePublications() {
		List<String> publicationsUris = new ArrayList<String>();
		String pubQueryString = generatePubQuery();
		try {
			RepositoryConnection connection = getRepository().getConnection();
			TupleQuery tupleQuery = connection.prepareTupleQuery(
					QueryLanguage.SPARQL, pubQueryString);
			TupleQueryResult result = tupleQuery.evaluate();
			try {
				while (result.hasNext()) {
					String uri = result.next()
							.getValue(RETRIEVED_PUBLICATION_URI_ELEMENT_KEY)
							.stringValue();
					publicationsUris.add(uri);
				}
			} finally {
				result.close();
				connection.close();
			}
		} catch (Exception e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"The publications list will be probably incomplete due to %s",
							e.getCause()));
		}
		return publicationsUris;
	}

	private List<Link> retrieveLinks(String documentUri)
			throws QueryEvaluationException, RepositoryException,
			MalformedQueryException {
		List<Link> links = new ArrayList<Link>();
		String linkQueryString = generateLinkQuery(documentUri);
		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, linkQueryString);
		TupleQueryResult result = tupleQuery.evaluate();
		try {
			while (result.hasNext()) {
				Link link = new Link();
				BindingSet solution = result.next();
				String linkType = solution.getValue(
						RETRIEVED_LINK_TYPE_ELEMENT_KEY).stringValue();
				link.setType(linkType);
				String linkHref = solution.getValue(
						RETRIEVED_LINK_HREF_ELEMENT_KEY).stringValue();
				link.setHref(linkHref);
				links.add(link);

			}
		} finally {
			result.close();
			connection.close();
		}
		return links;
	}

	private List<Author> retrieveAuthors(String documentUri)
			throws QueryEvaluationException, RepositoryException,
			MalformedQueryException {
		List<Author> authors = new ArrayList<Author>();
		String authorQueryString = generateAuthorQuery(documentUri);

		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, authorQueryString);
		TupleQueryResult result = tupleQuery.evaluate();

		try {
			while (result.hasNext()) {
				BindingSet author = result.next();
				Value authorNameValue = author
						.getValue(RETRIEVED_AUTHOR_NAME_ELEMENT_KEY);
				String authorName = authorNameValue.stringValue();
				Value affiliationValue = author
						.getValue(AFFILIATION_ELEMENT_KEY);
				String affiliation = affiliationValue != null ? affiliationValue
						.stringValue() : null;
				authors.add(new Author(authorName, affiliation));
			}
		} finally {
			result.close();
			connection.close();
		}
		return authors;
	}

	private String retrieveTitle(String documentUri)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String titleQueryString = generateTitleQuery(documentUri);

		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, titleQueryString);
		TupleQueryResult result = tupleQuery.evaluate();
		try {
			if (!result.hasNext())
				return null;
			BindingSet titleBindingSet = result.next();
			Value value = titleBindingSet.getValue(RETRIEVED_TITLE_ELEMENT_KEY);
			if (result.hasNext()) {
				logger.log(Level.INFO, String.format(
						"The document='%s' has more than one title.",
						documentUri));
			}

			return value.stringValue();
		} finally {
			result.close();
			connection.close();
		}
	}

	private Repository getRepository() throws RepositoryException {
		return this.repositoryProvider.get();
	}

	private String retrieveArxivId(String documentUri)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String idQueryString = generateArxivIdQuery(documentUri);

		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, idQueryString);
		TupleQueryResult result = tupleQuery.evaluate();

		try {
			if (!result.hasNext()) {
				return null;
			}
			Value resource = result.next().getValue(
					RETRIEVED_ARXIV_ID_ELEMENT_KEY);
			return resource.stringValue();
		} finally {
			result.close();
			connection.close();
		}

	}

	private boolean isPublication(String uri) throws QueryEvaluationException,
			RepositoryException, MalformedQueryException {
		String typeQueryString = generateTypeQueryString(uri);

		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, typeQueryString);
		TupleQueryResult result = tupleQuery.evaluate();

		boolean isPublication = false;
		try {
			while (result.hasNext()) {
				String typeUri = result.next().getValue("t").stringValue();
				if (typeUri.equals(PUBLICATION_TYPE_URI)) {
					isPublication = true;
					break;
				}
			}
		} finally {
			result.close();
			connection.close();
		}
		return isPublication;
	}

	private String parseDocumentUri(String resourceUri) {
		if (!resourceUri.contains(DELIMITER)) {
			return resourceUri;
		}
		return resourceUri.substring(0, resourceUri.indexOf(DELIMITER));
	}

	private String generateTypeQueryString(String uri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetTypes");
		return String.format(query, uri);
	}

	private String generateTitleQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetTitle");
		return String.format(query, documentUri);
	}

	private String generateArxivIdQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetArxivId");
		return String.format(query, documentUri);
	}

	private String generateSegmentInfoQuery(String segmentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetSegmentInfo");
		return String.format(query, segmentUri);
	}

	private String generateAuthorQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetAuthors");
		return String.format(query, documentUri);
	}

	private String generateLinkQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetLinks");
		return String.format(query, documentUri);
	}

	private String generatePubQuery() {
		return getSparqlQueryLoader().loadQueryByName("GetPublications");
	}

	private String generateRetrieveStructureGraphQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName(
				"GetStructureGraph");
		return String.format(query, documentUri);
	}

	private RDFGraph getSearchGraph() {
		return this.searchGraph;
	}

	public SparqlQueryLoader getSparqlQueryLoader() {
		return sparqlQueryLoader;
	}

	private static class SolutionFunction implements
			Function<BindingSet, SGEdge> {

		@Override
		public SGEdge apply(BindingSet solution) {
			int fromNumPage = ((Literal) solution.getValue("snumpage"))
					.intValue();
			int toNumPage = ((Literal) solution.getValue("onumpage"))
					.intValue();
			Value fromTitleValue = solution.getValue("stitle");
			String fromTitle = fromTitleValue != null ? fromTitleValue
					.stringValue() : null;
			Value toTitleValue = solution.getValue("otitle");
			String toTitle = toTitleValue != null ? toTitleValue.stringValue()
					: null;
			String subjectUri = solution.getValue(RETRIEVED_SUBJECT_GRAPH_NODE)
					.stringValue();
			String predicateUri = solution.getValue(
					RETRIEVED_PREDICATE_GRAPH_NODE).stringValue();
			String objectUri = solution.getValue(RETRIEVED_OBJECT_GRAPH_NODE)
					.stringValue();
			// TODO : retrieve titles of segments
			String subjectClass = solution.getValue(RETRIEVED_SUBJECT_CLASS)
					.stringValue();
			String objectClass = solution.getValue(RETRIEVED_OBJECT_CLASS)
					.stringValue();
			MocassinOntologyClasses subjectType = MocassinOntologyClasses
					.fromUri(subjectClass);
			MocassinOntologyClasses objectType = MocassinOntologyClasses
					.fromUri(objectClass);
			OntologyIndividual subject = new OntologyIndividual(subjectUri, "");
			subject.setType(subjectType);
			OntologyIndividual object = new OntologyIndividual(objectUri, "");
			object.setType(objectType);
			ABoxTriple triple = new ABoxTriple(subject,
					MocassinOntologyRelations.fromUri(predicateUri), object);
			return new SGEdge(triple, fromNumPage, toNumPage, fromTitle,
					toTitle);
		}

	}
}
