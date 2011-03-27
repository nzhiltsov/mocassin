package ru.ksu.niimm.ose.ontology;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;

public interface OntologyResourceFacade {
	/**
	 * load OMDoc element info for given resource
	 * 
	 * @param resource
	 *            resource
	 * @return
	 */
	ArticleMetadata load(OntologyResource resource);

	List<ArticleMetadata> loadAll();

	void insert(ArticleMetadata articleMetadata);

}