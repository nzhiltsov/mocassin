package ru.ksu.niimm.ose.ontology.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.ose.ontology.ArticleMetadata;
import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.SourceReference;
import ru.ksu.niimm.ose.ontology.loader.RDFGraphPropertiesLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Resource;

public class OMDocResourceFacadeImpl implements OMDocResourceFacade {
	private static final String RDF_PREFIX_STRING = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
	private static final String DC_TITLE_URI = "<http://purl.org/dc/elements/1.1/title>";
	private static final String OMDOC_DOCUMENT_URI = "<http://omdoc.org/ontology#Document>";
	private static final String RETRIEVED_TITLE_ELEMENT_KEY = "?2";
	private static final String DELIMITER = "#";
	@Inject
	private RDFGraphPropertiesLoader rdfGraphPropertiesLoader;
	@Inject
	private VirtuosoDAO virtuosoDAO;

	@Override
	public OMDocElement load(OntologyResource resource) {

		String resourceUri = resource.getUri();
		String documentUri = parseDocumentUri(resourceUri);
		String title = retrieveTitle(documentUri);

		OMDocElement omdocElement = new OMDocElement(resourceUri, null,
				new SourceReference());
		ArticleMetadata articleMetadata = new ArticleMetadata(documentUri);
		articleMetadata.setTitle(title);
		omdocElement.setArticleMetadata(articleMetadata);
		return omdocElement;
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
		String docExpression = String.format("?1 rdf:type %s",
				OMDOC_DOCUMENT_URI);
		String titleExpression = String.format("?1 %s %s", DC_TITLE_URI,
				RETRIEVED_TITLE_ELEMENT_KEY);
		String filterExpression = String.format("FILTER (str(?1) = \"%s\")",
				documentUri);
		return String.format("%s SELECT * WHERE {%s . %s . %s .}",
				RDF_PREFIX_STRING, docExpression, titleExpression,
				filterExpression);
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

}
