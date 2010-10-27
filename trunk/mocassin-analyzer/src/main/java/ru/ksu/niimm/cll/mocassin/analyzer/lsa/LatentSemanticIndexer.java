package ru.ksu.niimm.cll.mocassin.analyzer.lsa;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl.LSIndex;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

/**
 * LSA-based indexer
 * 
 * @author nzhiltsov
 * 
 */
public interface LatentSemanticIndexer {

	/**
	 * returns an index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	LSIndex buildReferenceIndex(List<Reference> references);
}
