package ru.ksu.niimm.cll.mocassin.virtuoso.generator;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public class N3Util {
	public static String getExpression(List<RDFTriple> triples) {
		StringBuffer constructTemplate = new StringBuffer();
		for (RDFTriple triple : triples) {
			String tripleStr = String.format("%s\n", triple.getValue());
			constructTemplate.append(tripleStr);
		}
		return constructTemplate.toString();
	}
}
