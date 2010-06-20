package ru.ksu.niimm.cll.mocassin.virtuoso.generator;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;

public interface DeleteQueryGenerator {
	String generate(String documentUri, RDFGraph graph);
}
