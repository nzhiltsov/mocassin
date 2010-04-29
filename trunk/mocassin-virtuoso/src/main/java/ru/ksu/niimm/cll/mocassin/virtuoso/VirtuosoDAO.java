package ru.ksu.niimm.cll.mocassin.virtuoso;

import java.util.List;

/**
 * DAO for Virtuoso RDF store
 * 
 * @author nzhiltsov
 * 
 */
public interface VirtuosoDAO {
	void delete(String documentUri, RDFGraph graph);

	void insert(List<RDFTriple> triples, RDFGraph graph);

	void update(String documentUri, List<RDFTriple> triples, RDFGraph graph);
}
