package ru.ksu.niimm.cll.mocassin.ui.viewer.server;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ArticleInfo;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ViewerService;
import ru.ksu.niimm.cll.mocassin.ui.viewer.server.util.OntologyElementConverter;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.SGEdge;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ViewerServiceImpl implements ViewerService {
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
	@Inject
	private OntologyElementConverter ontologyElementConverter;

	@Override
	public ArticleInfo load(String uri) {
		ArticleMetadata metadata = ontologyResourceFacade
				.load(new OntologyResource(uri));
		ArticleInfo info = new ArticleInfo();
		info.setKey(metadata.getCollectionId());
		info.setUri(metadata.getId());
		info.setTitle(metadata.getTitle());
		info.setCurrentSegmentUri(metadata.getCurrentSegmentUri());
		info.setCurrentPageNumber(metadata.getCurrentPageNumber());
		List<Author> authors = metadata.getAuthors();
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
