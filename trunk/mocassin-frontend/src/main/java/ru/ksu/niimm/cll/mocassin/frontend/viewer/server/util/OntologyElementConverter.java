package ru.ksu.niimm.cll.mocassin.frontend.viewer.server.util;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.SGEdge;

public interface OntologyElementConverter {
	Graph convert(List<SGEdge> triples);
}
