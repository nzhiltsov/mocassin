package ru.ksu.niimm.cll.mocassin.frontend.viewer.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.ArticleInfo;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.ViewerService;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.server.util.OntologyElementConverter;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Author;
import ru.ksu.niimm.cll.mocassin.util.model.Link;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ViewerServiceImpl implements ViewerService {
	@InjectLogger
	private Logger logger;
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
	@Inject
	private OntologyElementConverter ontologyElementConverter;

	@Override
	public ArticleInfo load(String uri) {
		ArticleMetadata metadata = ontologyResourceFacade
				.load(new OntologyResource(uri));
		if (metadata == null) {
			logger.warn("Not found metadata for URI='{}'. Stub will be returned", uri);
			metadata = new ArticleMetadata();
		}
		ArticleInfo info = new ArticleInfo();
		info.setKey(metadata.getCollectionId());
		info.setUri(metadata.getId());
		info.setTitle(metadata.getTitle());
		info.setCurrentSegmentUri(metadata.getCurrentSegmentUri());
		info.setCurrentPageNumber(metadata.getCurrentPageNumber());
		Set<Author> authors = metadata.getAuthors();
		List<String> authorNames = new ArrayList<String>();
		for (Author author : authors) {
			authorNames.add(author.getName());
		}
		info.setAuthors(authorNames);
		Link pdfLink = Iterables.find(metadata.getLinks(),
				new Link.PdfLinkPredicate(), Link.nullPdfLink());
		info.setPdfUri(pdfLink.getHref());
		return info;
	}

	@Override
	public Graph retrieveGraph(String uri) {
		List<SGEdge> triples = this.ontologyResourceFacade
				.retrieveStructureGraph(new OntologyResource(uri));
		return this.ontologyElementConverter.convert(triples);
	}

}
