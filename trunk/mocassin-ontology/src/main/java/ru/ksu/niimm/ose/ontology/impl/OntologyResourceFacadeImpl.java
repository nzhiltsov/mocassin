package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class OntologyResourceFacadeImpl implements OntologyResourceFacade {
	private static final String RETRIEVED_TITLE_ELEMENT_KEY = "?3";
	private static final String DELIMITER = "#";
	private static final String RETRIEVED_AUTHOR_NAME_ELEMENT_KEY = "?3";
	private static final String RETRIEVED_LINK_TYPE_ELEMENT_KEY = "?3";
	private static final String RETRIEVED_LINK_HREF_ELEMENT_KEY = "?4";
	private static final String RETRIEVED_PUBLICATION_URI_ELEMENT_KEY = "?1";
	@Inject
	private VirtuosoDAO virtuosoDAO;
	@Inject
	private SparqlQueryLoader sparqlQueryLoader;

	private RDFGraph searchGraph;

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
	}

	@Override
	public ArticleMetadata load(OntologyResource resource) {

		String documentUri = parseDocumentUri(resource.getUri());
		return load(documentUri);
	}

	private ArticleMetadata load(String documentUri) {
		String title = retrieveTitle(documentUri);
		List<Author> authors = retrieveAuthors(documentUri);
		List<Link> links = retrieveLinks(documentUri);

		ArticleMetadata articleMetadata = new ArticleMetadata();
		articleMetadata.setTitle(title);
		articleMetadata.setAuthors(authors);
		articleMetadata.setLinks(links);
		return articleMetadata;
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
	public void insert(ArticleMetadata articleMetadata) {
		List<RDFTriple> triples = ArxivMetadataUtil
				.convertToTriples(articleMetadata);
		getVirtuosoDAO().insert(triples, getSearchGraph());
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

	private RDFGraph getSearchGraph() {
		return this.searchGraph;
	}

	private VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public SparqlQueryLoader getSparqlQueryLoader() {
		return sparqlQueryLoader;
	}

}