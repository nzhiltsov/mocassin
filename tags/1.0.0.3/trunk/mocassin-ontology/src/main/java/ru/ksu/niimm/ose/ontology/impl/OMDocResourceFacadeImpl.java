package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.ose.ontology.ArticleMetadata;
import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;
import ru.ksu.niimm.ose.ontology.loader.SparqlQueryLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;

public class OMDocResourceFacadeImpl implements OMDocResourceFacade {
	private static final String RETRIEVED_TITLE_ELEMENT_KEY = "?2";
	private static final String DELIMITER = "#";
	private static final String RETRIEVED_AUTHOR_ELEMENT_KEY = "?2";

	@Inject
	private RDFGraphPropertiesLoader rdfGraphPropertiesLoader;
	@Inject
	private VirtuosoDAO virtuosoDAO;
	@Inject
	private SparqlQueryLoader sparqlQueryLoader;

	@Override
	public OMDocElement load(OntologyResource resource) {

		String resourceUri = resource.getUri();
		String documentUri = parseDocumentUri(resourceUri);
		String title = retrieveTitle(documentUri);
		List<String> authors = retrieveAuthors(documentUri);

		OMDocElement omdocElement = new OMDocElement(resourceUri, null,
				new SourceReference());
		ArticleMetadata articleMetadata = new ArticleMetadata(documentUri);
		articleMetadata.setTitle(title);
		articleMetadata.setAuthors(authors);
		omdocElement.setArticleMetadata(articleMetadata);
		return omdocElement;
	}

	private List<String> retrieveAuthors(String documentUri) {
		List<String> authors = new ArrayList<String>();
		String authorQueryString = generateAuthorQuery(documentUri);
		Query query = QueryFactory.create(authorQueryString);
		List<QuerySolution> solutions = getVirtuosoDAO().get(query, getGraph());
		for (QuerySolution solution : solutions) {
			Resource resource = solution
					.getResource(RETRIEVED_AUTHOR_ELEMENT_KEY);
			authors.add(resource.toString());
		}
		return authors;
	}

	private String retrieveTitle(String documentUri) {
		String titleQueryString = generateTitleQuery(documentUri);

		Query query = QueryFactory.create(titleQueryString);

		List<QuerySolution> solutions = getVirtuosoDAO().get(query, getGraph());
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

	private RDFGraph getGraph() {
		return getRdfGraphPropertiesLoader().getGraph();
	}

	private RDFGraphPropertiesLoader getRdfGraphPropertiesLoader() {
		return rdfGraphPropertiesLoader;
	}

	private VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public SparqlQueryLoader getSparqlQueryLoader() {
		return sparqlQueryLoader;
	}

}
