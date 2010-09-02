package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;
import java.util.Map;

public interface FeatureExtractor {
	Map<String, List<Feature>> getReference2FeatureMap() throws Exception;
}
