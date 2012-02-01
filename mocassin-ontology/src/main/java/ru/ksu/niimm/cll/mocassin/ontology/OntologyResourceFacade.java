package ru.ksu.niimm.cll.mocassin.ontology;

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
	 * @return null, if metadata is absent or couldn't be retrieved
	 */
	ArticleMetadata load(OntologyResource resource);

	List<ArticleMetadata> loadAll();

	/**
	 * load the structure graph for an article with a given resource description
	 * 
	 * @param resource
	 * @return
	 */
	List<SGEdge> retrieveStructureGraph(OntologyResource resource);

	/**
	 * 
	 * @param articleMetadata
	 * @param triples
	 * @return true, if update was successful
	 */
	boolean insert(ArticleMetadata articleMetadata, Set<RDFTriple> triples);

}