package ru.ksu.niimm.ose.ontology;

import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public interface OntologyResourceFacade {
	/**
	 * load metadata info for an article with a given resource description
	 * 
	 * @param resource
	 *            resource
	 * @return
	 */
	ArticleMetadata load(OntologyResource resource);

	List<ArticleMetadata> loadAll();

	/**
	 * load the structure graph for an article with a given resource description
	 * 
	 * @param resource
	 * @return
	 */
	List<ABoxTriple> retrieveStructureGraph(OntologyResource resource);

	void insert(ArticleMetadata articleMetadata, Set<RDFTriple> triples);

}