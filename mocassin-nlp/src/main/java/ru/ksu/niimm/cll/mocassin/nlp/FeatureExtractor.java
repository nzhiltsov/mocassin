package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

public interface FeatureExtractor {
	List<ReferenceContext> getReferenceContextList() throws Exception;
}
