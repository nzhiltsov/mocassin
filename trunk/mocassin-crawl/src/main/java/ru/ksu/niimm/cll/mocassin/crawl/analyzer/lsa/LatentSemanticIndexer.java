package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl.LSIndex;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

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
