package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class ArxivServiceImpl implements ArxivService {

	@Inject
	private ArxivDAOFacade arxivDAOFacade;
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;

	@Override
	public void handle(String arxivId) {
		ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
		ontologyResourceFacade.insert(metadata);
	}

	@Override
	public List<ArxivArticleMetadata> loadArticles() {
		List<ArticleMetadata> publications = ontologyResourceFacade.loadAll();
		List<ArxivArticleMetadata> articlesList = CollectionUtil
				.asList(Iterables.transform(publications,
						new ArxivArticleMetadataFunction()));
		return articlesList;
	}

	private static class ArxivArticleMetadataFunction implements
			Function<ArticleMetadata, ArxivArticleMetadata> {

		@Override
		public ArxivArticleMetadata apply(ArticleMetadata metadata) {
			return new ArxivArticleMetadata(metadata.getId(), metadata
					.getTitle());
		}

	}
}
