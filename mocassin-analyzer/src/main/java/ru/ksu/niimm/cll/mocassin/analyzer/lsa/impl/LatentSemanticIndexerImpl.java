package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.SvdMatrix;
import com.aliasi.matrix.Vector;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;

public class LatentSemanticIndexerImpl implements LatentSemanticIndexer {
	private static final int MAX_EPOCHS = 50000;
	private static final int MIN_EPOCHS = 10;
	private static final double MIN_IMPROVEMENT = 0.0000;
	private static final double REGULARIZATION = 0.00;
	private static final int ANNILING_RATE = 1000;
	private static final double INITIAL_LEARNING_RATE = 0.005;
	private static final double FEATURE_INIT = 0.01;
	private static final int MAX_FACTORS = 2;
	@Inject
	private StopWordLoader stopWordLoader;
	private Map<String, Integer> token2id;

	private BiMap<Reference, Integer> ref2id;

	@Override
	public Map<Reference, Vector> buildReferenceIndex(List<Reference> references) {
		this.token2id = new HashMap<String, Integer>();
		int tokenId = 0;
		this.ref2id = HashBiMap.create();
		int refId = 0;
		for (Reference ref : references) {
			List<String> sentenceTokens = ref.getSentenceTokens();
			for (String sentenceToken : sentenceTokens) {
				if (isStopWord(sentenceToken)
						|| this.token2id.containsKey(sentenceToken
								.toLowerCase()))
					continue;
				this.token2id.put(sentenceToken.toLowerCase(), tokenId);
				tokenId++;
			}
			this.ref2id.put(ref, refId);
			refId++;
		}
		BiMap<Integer, Reference> id2ref = this.ref2id.inverse();
		int m = this.token2id.size();
		int n = this.ref2id.size();
		double[][] matrix = new double[m][n];
		for (int j = 0; j < n; j++) {
			Reference ref = id2ref.get(j);
			List<String> sentenceTokens = ref.getSentenceTokens();
			for (String sentenceToken : sentenceTokens) {
				if (isStopWord(sentenceToken))
					continue;
				int i = this.token2id.get(sentenceToken.toLowerCase());
				matrix[i][j] += 1;
			}

		}

		SvdMatrix svdMatrix = SvdMatrix.svd(matrix, MAX_FACTORS, FEATURE_INIT,
				INITIAL_LEARNING_RATE, ANNILING_RATE, REGULARIZATION, null,
				MIN_IMPROVEMENT, MIN_EPOCHS, MAX_EPOCHS);
		double[][] refVectors = svdMatrix.rightSingularVectors();

		Map<Reference, Vector> referenceIndexMap = new HashMap<Reference, Vector>();
		for (int k = 0; k < refVectors.length; k++) {
			double[] column = new double[MAX_FACTORS];
			for (int l = 0; l < MAX_FACTORS; l++) {
				column[l] = refVectors[k][l];
			}
			Vector refVector = new DenseVector(column);
			referenceIndexMap.put(id2ref.get(k), refVector);
		}

		return referenceIndexMap;
	}

	public StopWordLoader getStopWordLoader() {
		return stopWordLoader;
	}

	public boolean isStopWord(String word) {
		return stopWordLoader.getStopWords().contains(word.toLowerCase());
	}

}
