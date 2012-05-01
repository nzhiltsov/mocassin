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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.LSIPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.SvdMatrix;
import com.aliasi.matrix.Vector;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class LatentSemanticIndexerImpl extends AbstractScoringIndexer implements
		LatentSemanticIndexer {
	private final int MAX_EPOCHS;
	private final int MIN_EPOCHS;
	private final double MIN_IMPROVEMENT;
	private final double REGULARIZATION;
	private final int ANNILING_RATE;
	private final double INITIAL_LEARNING_RATE;
	private final double FEATURE_INIT;
	private final int MAX_FACTORS;

	private LSIPropertiesLoader lsiPropertiesLoader;

	@Inject
	public LatentSemanticIndexerImpl(LSIPropertiesLoader lsiPropertiesLoader) {
		this.lsiPropertiesLoader = lsiPropertiesLoader;
		MAX_EPOCHS = lsiPropertiesLoader.getMaxEpochs();
		MIN_EPOCHS = lsiPropertiesLoader.getMinEpochs();
		MIN_IMPROVEMENT = lsiPropertiesLoader.getMinImprovement();
		REGULARIZATION = lsiPropertiesLoader.getRegularization();
		ANNILING_RATE = lsiPropertiesLoader.getAnilingRate();
		INITIAL_LEARNING_RATE = lsiPropertiesLoader.getInitialLearningRate();
		FEATURE_INIT = lsiPropertiesLoader.getFeatureInit();
		MAX_FACTORS = lsiPropertiesLoader.getMaxFactors();
	}

	public LSIPropertiesLoader getLsiPropertiesLoader() {
		return lsiPropertiesLoader;
	}

	@Override
	public LSIndex buildReferenceIndex(List<Reference> references) {
		initializeIndices(references);
		BiMap<Integer, Reference> id2ref = this.ref2id.inverse();
		int m = this.token2id.size();
		int n = this.ref2id.size();
		double[][] matrix = buildWeightedTermReferenceMatrix(id2ref, m, n);

		SvdMatrix svdMatrix = SvdMatrix.svd(matrix, MAX_FACTORS, FEATURE_INIT,
				INITIAL_LEARNING_RATE, ANNILING_RATE, REGULARIZATION, null,
				MIN_IMPROVEMENT, MIN_EPOCHS, MAX_EPOCHS);
		Map<Reference, Vector> referenceIndexMap = getReferenceIndexMap(id2ref,
				svdMatrix);

		Map<String, Vector> termIndexMap = getTermIndexMap(svdMatrix);

		return new LSIndexImpl(referenceIndexMap, termIndexMap,
				new ArrayList<String>(this.token2id.keySet()));
	}

	@Override
	protected List<Token> filterTokens(List<Token> tokens) {
		Predicate<Token> filter = new Predicate<Token>() {

			@Override
			public boolean apply(Token token) {
				if (!getLsiPropertiesLoader().useStopWords()
						&& isStopWord(token.getValue()))
					return false;
				return true;
			}
		};

		return CollectionUtil.asList(Iterables.filter(tokens, filter));
	}

	private Map<String, Vector> getTermIndexMap(SvdMatrix svdMatrix) {
		double[][] termVectors = svdMatrix.leftSingularVectors();

		BiMap<Integer, String> id2token = this.token2id.inverse();
		Map<String, Vector> termIndexMap = computeIndexMap(termVectors,
				id2token);
		return termIndexMap;
	}

	private Map<Reference, Vector> getReferenceIndexMap(
			BiMap<Integer, Reference> id2ref, SvdMatrix svdMatrix) {
		double[][] refVectors = svdMatrix.rightSingularVectors();

		Map<Reference, Vector> referenceIndexMap = computeIndexMap(refVectors,
				id2ref);
		return referenceIndexMap;
	}

	private <K> Map<K, Vector> computeIndexMap(double[][] vectors,
			BiMap<Integer, K> biMap) {
		Map<K, Vector> indexMap = new HashMap<K, Vector>();
		for (int k = 0; k < vectors.length; k++) {
			double[] column = new double[MAX_FACTORS];
			for (int l = 0; l < MAX_FACTORS; l++) {
				column[l] = vectors[k][l];
			}
			Vector termVector = new DenseVector(column);
			indexMap.put(biMap.get(k), termVector);
		}
		return indexMap;
	}

}
