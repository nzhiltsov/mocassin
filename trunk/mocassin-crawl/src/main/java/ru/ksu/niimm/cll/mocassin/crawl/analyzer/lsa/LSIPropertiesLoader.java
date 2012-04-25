package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa;

public interface LSIPropertiesLoader {
	/**
	 * should LSI filter out the stop words
	 * 
	 * @return
	 */
	boolean useStopWords();

	int getMaxFactors();

	double getFeatureInit();

	double getInitialLearningRate();

	int getAnilingRate();

	double getRegularization();

	double getMinImprovement();

	int getMinEpochs();

	int getMaxEpochs();
}
