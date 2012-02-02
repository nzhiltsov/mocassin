package ru.ksu.niimm.cll.mocassin.virtuoso.generator;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;

public interface DescribeQueryGenerator {
	String generate(String resourceUri, RDFGraph graph);
	
	String generate(String resourceUri);
}
