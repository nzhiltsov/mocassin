package ru.ksu.niimm.cll.mocassin.analyzer.pos;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndex;
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
	 * returns an index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	WeightedIndex buildReferenceIndex(List<Reference> references);
}
