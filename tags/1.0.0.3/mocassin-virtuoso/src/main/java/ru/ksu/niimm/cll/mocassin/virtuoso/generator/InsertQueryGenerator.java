package ru.ksu.niimm.cll.mocassin.virtuoso.generator;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public interface InsertQueryGenerator {
	String generate(List<RDFTriple> triple, RDFGraph graph);
}
