package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.ose.ontology.ABoxTriple;
import ru.ksu.niimm.ose.ontology.OntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;

public class OntologyResourceFacadeImpl implements OntologyResourceFacade {
	private static final String RETRIEVED_OBJECT_CLASS = "?oclass";
	private static final String RETRIEVED_SUBJECT_CLASS = "?sclass";
	private static final String RETRIEVED_OBJECT_GRAPH_NODE = "?o";
	private static final String RETRIEVED_PREDICATE_GRAPH_NODE = "?p";
	private static final String RETRIEVED_SUBJECT_GRAPH_NODE = "?s";
	private static final String RETRIEVED_TITLE_ELEMENT_KEY = "?3";
	private static final String DELIMITER = "#";
	private static final String RETRIEVED_AUTHOR_NAME_ELEMENT_KEY = "?3";
	private static final String RETRIEVED_LINK_TYPE_ELEMENT_KEY = "?3";
	private static final String RETRIEVED_LINK_HREF_ELEMENT_KEY = "?4";
	private static final String RETRIEVED_PUBLICATION_URI_ELEMENT_KEY = "?1";
	private static final String RULES_SET_ENTRY = "define input:inference \"%s\"";
	@Inject
	private VirtuosoDAO virtuosoDAO;
	@Inject
	private SparqlQueryLoader sparqlQueryLoader;
	@Inject
	private OntologyFacade ontologyFacade;

	private RDFGraph searchGraph;

	private String ontologyRulesSetName;

	@Inject
	public OntologyResourceFacadeImpl(
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password,
			@Named("graph.iri") String graphIri,
			@Named("ontology.rules.set") String ontologyRuleSet) {
		this.searchGraph = new RDFGraphImpl.Builder(graphIri)
				.url(connectionUrl).username(username).password(password)
				.build();
		this.ontologyRulesSetName = ontologyRuleSet;
	}

	@Override
	public ArticleMetadata load(OntologyResource resource) {

		String documentUri = parseDocumentUri(resource.getUri());
		return load(documentUri);
	}

	@Override
	public List<ABoxTriple> retrieveStructureGraph(OntologyResource resource) {
		List<ABoxTriple> triples = new ArrayList<ABoxTriple>();
		String documentUri = parseDocumentUri(resource.getUri());
		String graphQueryString = generateRetrieveStructureGraphQuery(documentUri);
		Iterator<QuerySolution> solutionIt = getVirtuosoDAO().get(
				graphQueryString, getSearchGraph()).iterator();
		if (!solutionIt.hasNext())
			return triples;
		Function<QuerySolution, ABoxTriple> function = new SolutionFunction();
		ABoxTriple curTriple = function.apply(solutionIt.next());

		while (solutionIt.hasNext()) {
			ABoxTriple aboxTriple = function.apply(solutionIt.next());
			if (aboxTriple.equals(curTriple)) {
				MocassinOntologyClasses curSubjectType = curTriple.getSubject()
						.getType();
				MocassinOntologyClasses curObjectType = curTriple.getObject()
						.getType();
				MocassinOntologyClasses subjectType = aboxTriple.getSubject()
						.getType();
				MocassinOntologyClasses objectType = aboxTriple.getObject()
						.getType();
				boolean isMoreSpecificSubject = subjectType
						.equals(this.ontologyFacade.getMoreSpecific(
								curSubjectType, subjectType));
				boolean isMoreSpecificObject = objectType
						.equals(this.ontologyFacade.getMoreSpecific(
								curObjectType, objectType));
				if (isMoreSpecificSubject && isMoreSpecificObject) {
					curTriple = aboxTriple;
				}
			} else {
				triples.add(curTriple);
				curTriple = aboxTriple;
			}
		}
		if (!triples.contains(curTriple))
			triples.add(curTriple);
		return triples;
	}

	@Override
	public List<ArticleMetadata> loadAll() {
		List<ArticleMetadata> list = new ArrayList<ArticleMetadata>();
		List<String> publicationsUris = retrievePublications();
		for (String uri : publicationsUris) {
			ArticleMetadata metadata = load(uri);
			list.add(metadata);
		}
		return list;
	}

	@Override
	public void insert(ArticleMetadata articleMetadata, Set<RDFTriple> data) {
		List<RDFTriple> triples = ArxivMetadataUtil
				.convertToTriples(articleMetadata);
		triples.addAll(data);
		getVirtuosoDAO().insert(triples, getSearchGraph());
	}

	private ArticleMetadata load(String documentUri) {
		String title = retrieveTitle(documentUri);
		List<Author> authors = retrieveAuthors(documentUri);
		List<Link> links = retrieveLinks(documentUri);

		ArticleMetadata articleMetadata = new ArticleMetadata();
		articleMetadata.setId(documentUri);
		articleMetadata.setTitle(title);
		articleMetadata.setAuthors(authors);
		articleMetadata.setLinks(links);
		return articleMetadata;
	}

	private List<String> retrievePublications() {
		List<String> publicationsUris = new ArrayList<String>();
		String pubQueryString = generatePubQuery();
		Query query = QueryFactory.create(pubQueryString);
		List<QuerySolution> solutions = getVirtuosoDAO().get(query,
				getSearchGraph());
		for (QuerySolution solution : solutions) {
			String uri = solution.getResource(
					RETRIEVED_PUBLICATION_URI_ELEMENT_KEY).toString();
			publicationsUris.add(uri);
		}
		return publicationsUris;
	}

	private List<Link> retrieveLinks(String documentUri) {
		List<Link> links = new ArrayList<Link>();
		String linkQueryString = generateLinkQuery(documentUri);
		Query query = QueryFactory.create(linkQueryString);
		List<QuerySolution> solutions = getVirtuosoDAO().get(query,
				getSearchGraph());
		for (QuerySolution solution : solutions) {
			Link link = new Link();
			String linkType = solution.getResource(
					RETRIEVED_LINK_TYPE_ELEMENT_KEY).toString();
			link.setType(linkType);
			String linkHref = solution.getResource(
					RETRIEVED_LINK_HREF_ELEMENT_KEY).toString();
			link.setHref(linkHref);
			links.add(link);

		}
		return links;
	}

	private List<Author> retrieveAuthors(String documentUri) {
		List<Author> authors = new ArrayList<Author>();
		String authorQueryString = generateAuthorQuery(documentUri);
		Query query = QueryFactory.create(authorQueryString);
		List<QuerySolution> solutions = getVirtuosoDAO().get(query,
				getSearchGraph());
		for (QuerySolution solution : solutions) {
			Resource resource = solution
					.getResource(RETRIEVED_AUTHOR_NAME_ELEMENT_KEY);
			String authorName = resource.toString();
			/**
			 * TODO extract an author's affiliation, either
			 */
			authors.add(new Author(authorName, ""));
		}
		return authors;
	}

	private String retrieveTitle(String documentUri) {
		String titleQueryString = generateTitleQuery(documentUri);

		Query query = QueryFactory.create(titleQueryString);

		List<QuerySolution> solutions = getVirtuosoDAO().get(query,
				getSearchGraph());
		if (solutions.isEmpty()) {
			return null;
		}
		Resource resource = solutions.get(0).getResource(
				RETRIEVED_TITLE_ELEMENT_KEY);
		return resource.toString();
	}

	private String parseDocumentUri(String resourceUri) {
		if (!resourceUri.contains(DELIMITER)) {
			return resourceUri;
		}
		return resourceUri.substring(0, resourceUri.indexOf(DELIMITER));
	}

	private String generateTitleQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetTitle");
		return String.format(query, documentUri);
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

	private String generateTypeQuery(String uri) {
		String query = getSparqlQueryLoader().loadQueryByName("GetTypes");
		return String.format(query, uri);
	}

	private String generateRetrieveStructureGraphQuery(String documentUri) {
		String query = getSparqlQueryLoader().loadQueryByName(
				"GetStructureGraph");
		StringBuffer sb = new StringBuffer(String.format(RULES_SET_ENTRY,
				this.ontologyRulesSetName));
		sb.append("\n");
		sb.append(String.format(query, documentUri));
		return sb.toString();
	}

	private RDFGraph getSearchGraph() {
		return this.searchGraph;
	}

	private VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public SparqlQueryLoader getSparqlQueryLoader() {
		return sparqlQueryLoader;
	}

	private static class SolutionFunction implements
			Function<QuerySolution, ABoxTriple> {

		@Override
		public ABoxTriple apply(QuerySolution solution) {
			String subjectUri = solution.getResource(
					RETRIEVED_SUBJECT_GRAPH_NODE).toString();
			String predicateUri = solution.getResource(
					RETRIEVED_PREDICATE_GRAPH_NODE).toString();
			String objectUri = solution
					.getResource(RETRIEVED_OBJECT_GRAPH_NODE).toString();
			// TODO : retrieve titles of segments
			String subjectClass = solution.getResource(RETRIEVED_SUBJECT_CLASS)
					.toString();
			String objectClass = solution.getResource(RETRIEVED_OBJECT_CLASS)
					.toString();
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
			return triple;
		}

	}
}