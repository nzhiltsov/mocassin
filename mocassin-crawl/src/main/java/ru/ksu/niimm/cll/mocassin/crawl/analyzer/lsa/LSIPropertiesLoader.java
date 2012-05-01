/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
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
