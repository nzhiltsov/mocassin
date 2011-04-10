package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;
import ru.ksu.niimm.ose.ontology.OntologyTriple;

public interface OntologyElementConverter {
	Graph convert(List<OntologyTriple> triples);
}
