/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.rdf.ontology.impl;

import static java.lang.String.format;
import static ru.ksu.niimm.cll.mocassin.util.StringUtil.extractDocumentURIFromSegmentURI;
import static ru.ksu.niimm.cll.mocassin.util.StringUtil.extractMathnetKeyFromURI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.ABoxTriple;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.loader.SparqlQueryLoader;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Author;
import ru.ksu.niimm.cll.mocassin.util.model.Link;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class OntologyResourceFacadeImpl implements OntologyResourceFacade {
	private static final String AUTHOR_URI_ELEMENT_KEY = "2";
	private static final String LOCALE = "ru"; // TODO: make adjustable
												// parameter
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

	private final SparqlQueryLoader sparqlQueryLoader;

	private final OntologyFacade ontologyFacade;

	private final RepositoryProvider<Repository> repositoryProvider;
	@InjectLogger
	private Logger logger;

	private final RDFGraph searchGraph;

	private final String ontologyRulesSetName;

	@Inject
	public OntologyResourceFacadeImpl(SparqlQueryLoader sparqlQueryLoader,
			OntologyFacade ontologyFacade,
			RepositoryProvider<Repository> repositoryProvider,
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password,
			@Named("graph.iri") String graphIri,
			@Named("ontology.rules.set") String ontologyRuleSet) {
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
			logger.error(
					"Failed to check if the document= {} is a publication.",
					resourceUri, e);
			return null;
		}
		if (isPublication) {
			try {
				return loadPublication(resourceUri);
			} catch (Exception e) {
				logger.error("Failed to load the document= {}", resourceUri, e);
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
					logger.error(
							"None of a title and publication metadata was found for a segment: {}",
							resourceUri);
					return null;
				}
				BindingSet segmentInfoBindingSet = segmentInfoResult.next();
				Value segmentTitleValue = segmentInfoBindingSet
						.getValue("stitle");
				segmentTitle = segmentTitleValue != null ? segmentTitleValue
						.stringValue() : null;
				documentUri = extractDocumentURIFromSegmentURI(resourceUri);
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
			logger.error("Failed to load info of the segment= {}", resourceUri,
					e);
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
			logger.error(
					"Failed to retrieve the structural graph of the document= {}",
					resource.getUri(), e);
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
				logger.error("Couldn't load metadata of the document= {}", uri,
						e);
			}
		}
		return list;
	}

	@Override
	public boolean insert(List<Statement> statements) {

		try {
			RepositoryConnection connection = getRepository().getConnection();
			try {
				URI context = getRepository().getValueFactory().createURI(
						getSearchGraph().getIri());
				connection.add(statements, context);
				logger.info(
						"{} triple(s) have been inserted into the triple store.",
						statements.size());
			} finally {
				connection.close();
			}
			return true;
		} catch (Exception e) {
			logger.error("Failed to insert triples.", e);
			return false;
		}
	}

	private ArticleMetadata loadPublication(String documentUri)
			throws RepositoryException, MalformedQueryException,
			QueryEvaluationException {
		String title = retrieveTitle(documentUri);
		// String arxivId = retrieveArxivId(documentUri); // TODO: workaround
		// for Mathnet keys
		String mathnetKey = extractMathnetKeyFromURI(documentUri);
		Set<Author> authors = retrieveAuthors(documentUri);
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
			logger.error("The publications list will be probably incomplete.",
					e);
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

	private Set<Author> retrieveAuthors(String documentUri)
			throws QueryEvaluationException, RepositoryException,
			MalformedQueryException {
		Set<Author> authors = new HashSet<Author>();
		String authorQueryString = generateAuthorQuery(documentUri);

		RepositoryConnection connection = getRepository().getConnection();
		TupleQuery tupleQuery = connection.prepareTupleQuery(
				QueryLanguage.SPARQL, authorQueryString);
		TupleQueryResult result = tupleQuery.evaluate();

		try {
			while (result.hasNext()) {
				BindingSet author = result.next();
				Value uriValue = author.getValue(AUTHOR_URI_ELEMENT_KEY);
				String authorUri = uriValue.stringValue();
				Value authorNameValue = author
						.getValue(RETRIEVED_AUTHOR_NAME_ELEMENT_KEY);
				String authorName = authorNameValue.stringValue();
				Value affiliationValue = author
						.getValue(AFFILIATION_ELEMENT_KEY);
				String affiliation = affiliationValue != null ? affiliationValue
						.stringValue() : null;
				authors.add(new Author(authorUri, authorName, affiliation));
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
				logger.warn(
						"The document= {} has more than one title. Perhaps, the RDF graph is inconsistent.",
						documentUri);
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
		return format(query, getSearchGraph().getIri(), uri);
	}

	private String generateTitleQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetTitle");
		return format(query, getSearchGraph().getIri(), documentUri);
	}

	private String generateArxivIdQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetArxivId");
		return format(query, getSearchGraph().getIri(), documentUri);
	}

	private String generateSegmentInfoQuery(String segmentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetSegmentInfo");
		return format(query, getSearchGraph().getIri(), segmentUri, segmentUri);
	}

	private String generateAuthorQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetAuthors");
		return format(query, getSearchGraph().getIri(), documentUri, LOCALE);
	}

	private String generateLinkQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetLinks");
		return format(query, getSearchGraph().getIri(), documentUri);
	}

	private String generatePubQuery() {
		String query = getSparqlQueryLoader()
				.loadQueryByName("GetPublications");
		return format(query, getSearchGraph().getIri());
	}

	private String generateRetrieveStructureGraphQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName(
				"GetStructureGraph");
		return format(query, getSearchGraph().getIri(), documentUri);
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
