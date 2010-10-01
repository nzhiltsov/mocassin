package ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIPropertiesLoader;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.LSIndex;
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
	private final int MAX_EPOCHS;
	private final int MIN_EPOCHS;
	private final double MIN_IMPROVEMENT;
	private final double REGULARIZATION;
	private final int ANNILING_RATE;
	private final double INITIAL_LEARNING_RATE;
	private final double FEATURE_INIT;
	private final int MAX_FACTORS;

	@Inject
	private StopWordLoader stopWordLoader;

	private BiMap<String, Integer> token2id;

	private BiMap<Reference, Integer> ref2id;

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
		double[][] matrix = buildTermReferenceMatrix(id2ref, m, n);

		SvdMatrix svdMatrix = SvdMatrix.svd(matrix, MAX_FACTORS, FEATURE_INIT,
				INITIAL_LEARNING_RATE, ANNILING_RATE, REGULARIZATION, null,
				MIN_IMPROVEMENT, MIN_EPOCHS, MAX_EPOCHS);
		Map<Reference, Vector> referenceIndexMap = getReferenceIndexMap(id2ref,
				svdMatrix);

		Map<String, Vector> termIndexMap = getTermIndexMap(svdMatrix);

		return new LSIndexImpl(referenceIndexMap, termIndexMap);
	}

	private void initializeIndices(List<Reference> references) {
		this.token2id = HashBiMap.create();
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
	}

	private double[][] buildTermReferenceMatrix(
			BiMap<Integer, Reference> id2ref, int m, int n) {
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
		return matrix;
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

	public StopWordLoader getStopWordLoader() {
		return stopWordLoader;
	}

	public boolean isStopWord(String word) {
		if (!getLsiPropertiesLoader().useStopWords())
			return false;
		return stopWordLoader.getStopWords().contains(word.toLowerCase());
	}

}
