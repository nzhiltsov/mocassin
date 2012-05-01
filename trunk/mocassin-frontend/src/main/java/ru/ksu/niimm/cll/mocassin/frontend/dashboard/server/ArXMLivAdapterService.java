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
package ru.ksu.niimm.cll.mocassin.frontend.dashboard.server;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;
import ru.ksu.niimm.cll.mocassin.frontend.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.frontend.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;

import com.google.inject.Inject;

public class ArXMLivAdapterService implements ArxivService {
	@Inject
	protected OntologyResourceFacade ontologyResourceFacade;

	@Override
	public PagingLoadInfo<ArxivArticleMetadata> loadArticles(
			PagingLoadConfig pagingLoadConfig) {
		List<ArxivArticleMetadata> articles = loadArticles();
		PagingLoadConfig adjustedPagingLoadConfig = PagingLoadConfig
				.adjustPagingLoadConfig(pagingLoadConfig, articles.size());
		List<ArxivArticleMetadata> filteredArticles = new ArrayList<ArxivArticleMetadata>(
				articles.subList(adjustedPagingLoadConfig.getOffset(),
						adjustedPagingLoadConfig.getOffset()
								+ adjustedPagingLoadConfig.getLimit()));
		PagingLoadInfo<ArxivArticleMetadata> pagingLoadInfo = new PagingLoadInfo<ArxivArticleMetadata>();
		pagingLoadInfo.setPagingLoadConfig(pagingLoadConfig);
		pagingLoadInfo.setData(filteredArticles);
		pagingLoadInfo.setFullCollectionSize(articles.size());
		return pagingLoadInfo;
	}

	private List<ArxivArticleMetadata> loadArticles() {
		List<ArticleMetadata> publications = ontologyResourceFacade.loadAll();
		List<ArxivArticleMetadata> articlesList = new ArrayList<ArxivArticleMetadata>();
		for (ArticleMetadata metadata : publications) {
			articlesList.add(new ArxivArticleMetadata(metadata.getId(),
					metadata.getTitle()));
		}
		return articlesList;
	}

}
