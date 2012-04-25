package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.SGEdge;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;

public interface OntologyElementConverter {
	Graph convert(List<SGEdge> triples);
}
