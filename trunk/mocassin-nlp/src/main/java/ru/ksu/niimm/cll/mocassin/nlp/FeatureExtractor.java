package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;
import java.util.Map;

/**
 * Feature extractor to process whole document corpus
 * 
 * @author nzhiltsov
 * 
 */
public interface FeatureExtractor {
	Map<String, List<Reference>> getReferencesPerDocument() throws Exception;
}
