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
	 * process all the documents from given corpus (see parameters in a
	 * configuration file)
	 * 
	 * @throws Exception
	 */
	void processReferences() throws Exception;

	void addListener(ReferenceProcessListener listener);
}
