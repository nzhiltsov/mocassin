package ru.ksu.niimm.cll.mocassin.analyzer.lsa;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public interface LatentSemanticIndexer {
	/**
	 * returns latent semantic index for given collections of references
	 * 
	 * @param references
	 * @param stopWords
	 * @return
	 */
	LSIndex buildReferenceIndex(List<Reference> references);
}
