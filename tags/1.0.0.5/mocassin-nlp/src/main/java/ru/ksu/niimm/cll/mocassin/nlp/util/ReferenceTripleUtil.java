package ru.ksu.niimm.cll.mocassin.nlp.util;

import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceTripleUtil {
	Set<RDFTriple> convert(Graph<StructuralElement, Reference> references);
}
