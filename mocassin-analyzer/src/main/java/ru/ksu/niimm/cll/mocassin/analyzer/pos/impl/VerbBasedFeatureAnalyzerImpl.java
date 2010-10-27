package ru.ksu.niimm.cll.mocassin.analyzer.pos.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndex;
import ru.ksu.niimm.cll.mocassin.analyzer.indexers.WeightedIndexImpl;
import ru.ksu.niimm.cll.mocassin.analyzer.lsa.impl.AbstractScoringIndexer;
import ru.ksu.niimm.cll.mocassin.analyzer.pos.VerbBasedFeatureAnalyzer;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateFormatConstants;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.aliasi.matrix.DenseVector;
import com.aliasi.matrix.Vector;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;

public class VerbBasedFeatureAnalyzerImpl extends AbstractScoringIndexer
		implements VerbBasedFeatureAnalyzer {

	@Override
	public WeightedIndex buildReferenceIndex(List<Reference> references) {
		initializeIndices(references);
		BiMap<Integer, Reference> id2ref = this.ref2id.inverse();
		int m = this.token2id.size();
		int n = this.ref2id.size();
		double[][] matrix = buildWeightedTermReferenceMatrix(id2ref, m, n);
		Map<Reference, Vector> referenceIndexMap = new HashMap<Reference, Vector>();
		for (int k = 0; k < n; k++) {
			double[] column = new double[m];
			for (int l = 0; l < m; l++) {
				column[l] = matrix[l][k];
			}
			Vector vector = new DenseVector(column);
			referenceIndexMap.put(id2ref.get(k), vector);
		}
		Map<Integer, String> id2token = this.token2id.inverse();
		List<Integer> ids = new ArrayList<Integer>(id2token.keySet());
		Collections.sort(ids);
		List<String> terms = new LinkedList<String>();
		for (Integer id : ids) {
			terms.add(id2token.get(id));
		}
		return new WeightedIndexImpl(referenceIndexMap, terms);
	}

	@Override
	protected List<Token> filterTokens(List<Token> tokens) {
		Predicate<Token> filter = new Predicate<Token>() {

			@Override
			public boolean apply(Token token) {
				if (isStopWord(token.getValue()))
					return false;
				String pos = token.getPos();
				if (pos == null)
					return false;
				return pos.toLowerCase().startsWith(
						GateFormatConstants.VERB_POS_TAG_PREFIX);
			}
		};

		return CollectionUtil.asList(Iterables.filter(tokens, filter));
	}

}
