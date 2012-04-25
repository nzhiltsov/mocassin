package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.util.List;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;

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
	 * @param statements
	 * @return true, if the insertion was successful
	 */
	boolean insert(List<Statement> statements);

}