package ru.ksu.niimm.cll.mocassin.analyzer.pos;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.Index;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

/**
 * This indexer takes into account only verb tokens of a referential sentence
 * using information about their POS tags.
 * 
 * 
 * @author nzhiltsov
 * 
 */
public interface VerbBasedFeatureAnalyzer {

	/**
	 * returns a TF-IDF weighted vector index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	Index buildReferenceWeightedIndex(List<Reference> references);

	/**
	 * returns a boolean vector index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	Index buildReferenceBooleanIndex(List<Reference> references);
}
