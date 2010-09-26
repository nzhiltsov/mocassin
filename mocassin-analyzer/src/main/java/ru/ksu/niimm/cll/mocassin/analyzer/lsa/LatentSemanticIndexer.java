package ru.ksu.niimm.cll.mocassin.analyzer.lsa;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aliasi.dict.Dictionary;
import com.aliasi.matrix.Vector;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface LatentSemanticIndexer {
	/**
	 * returns latent semantic index for given collections of references
	 * 
	 * @param references
	 * @param stopWords
	 * @return
	 */
	Map<Reference, Vector> buildReferenceIndex(List<Reference> references);
}
