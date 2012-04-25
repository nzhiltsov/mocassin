package ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.StopWordLoader;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;

public abstract class AbstractScoringIndexer {

	@Inject
	private StopWordLoader stopWordLoader;
	protected BiMap<String, Integer> token2id;
	protected BiMap<Reference, Integer> ref2id;

	protected abstract List<Token> filterTokens(List<Token> tokens);

	protected void initializeIndices(List<Reference> references) {
		this.token2id = HashBiMap.create();
		int tokenId = 0;
		this.ref2id = HashBiMap.create();
		int refId = 0;
		for (Reference ref : references) {
			List<Token> sentenceTokens = filterTokens(ref.getSentenceTokens());
			for (Token sentenceToken : sentenceTokens) {
				if (this.token2id.containsKey(sentenceToken.getValue()
						.toLowerCase()))
					continue;
				this.token2id.put(sentenceToken.getValue().toLowerCase(),
						tokenId);
				tokenId++;
			}
			this.ref2id.put(ref, refId);
			refId++;
		}
	}

	/**
	 * build term/reference matrix with boolean values
	 * 
	 * @param id2ref
	 *            identifier-to-reference map
	 * @param m
	 *            count of terms
	 * @param n
	 *            count of references
	 * @return
	 */
	protected double[][] buildBooleanTermReferenceMatrix(
			BiMap<Integer, Reference> id2ref, int m, int n) {
		double[][] matrix = new double[m][n];
		for (int j = 0; j < n; j++) {
			Reference ref = id2ref.get(j);
			List<Token> sentenceTokens = filterTokens(ref.getSentenceTokens());
			for (Token sentenceToken : sentenceTokens) {
				int i = this.token2id.get(sentenceToken.getValue()
						.toLowerCase());
				if (matrix[i][j] == 0) {
					matrix[i][j] = 1;
				}

			}

		}
		return matrix;
	}

	/**
	 * build term/reference matrix with values weighted using tf-idf
	 * 
	 * @param id2ref
	 *            identifier-to-reference map
	 * @param m
	 *            count of terms
	 * @param n
	 *            count of references
	 * @return
	 */
	protected double[][] buildWeightedTermReferenceMatrix(
			BiMap<Integer, Reference> id2ref, int m, int n) {
		double[][] matrix = new double[m][n];
		for (int j = 0; j < n; j++) {
			Reference ref = id2ref.get(j);
			List<Token> sentenceTokens = filterTokens(ref.getSentenceTokens());
			for (Token sentenceToken : sentenceTokens) {
				int i = this.token2id.get(sentenceToken.getValue()
						.toLowerCase());
				matrix[i][j] += 1;
			}

		}
		double[][] weightedMatrix = new double[m][n];

		for (int k = 0; k < m; k++) {
			for (int l = 0; l < n; l++) {
				if (matrix[k][l] == 0)
					continue;
				int termCount = 0;
				for (int s = 0; s < m; s++) {
					termCount += matrix[s][l];
				}
				double tf = matrix[k][l] / termCount;

				int docCount = 0;
				for (int s = 0; s < n; s++) {
					if (matrix[k][s] > 0) {
						docCount++;
					}
				}
				double idf = ((double) n) / docCount;
				weightedMatrix[k][l] = tf * idf;
			}
		}

		return weightedMatrix;
	}

	public StopWordLoader getStopWordLoader() {
		return stopWordLoader;
	}

	public boolean isStopWord(String word) {
		return stopWordLoader.getStopWords().contains(word.toLowerCase());
	}

}