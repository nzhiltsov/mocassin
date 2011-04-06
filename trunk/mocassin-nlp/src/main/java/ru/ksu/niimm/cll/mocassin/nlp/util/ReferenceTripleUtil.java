package ru.ksu.niimm.cll.mocassin.nlp.util;

import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;

public interface ReferenceTripleUtil {
	Set<RDFTriple> convert(List<Reference> references);
}
