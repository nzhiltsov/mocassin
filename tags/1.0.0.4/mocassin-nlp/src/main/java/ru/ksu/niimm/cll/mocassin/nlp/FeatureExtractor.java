package ru.ksu.niimm.cll.mocassin.nlp;

import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceProcessListener;

/**
 * Feature extractor to process whole document corpus
 * 
 * @author nzhiltsov
 * 
 */
public interface FeatureExtractor {
	/**
	 * process a given number (zero count means "all") of the documents randomly selected from given corpus (see parameters in a
	 * configuration file)
	 * 
	 * @throws Exception
	 */
	void processReferences(int count) throws Exception;

	void addListener(ReferenceProcessListener listener);
}
