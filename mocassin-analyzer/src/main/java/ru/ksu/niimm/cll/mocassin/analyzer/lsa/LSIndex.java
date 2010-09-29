package ru.ksu.niimm.cll.mocassin.analyzer.lsa;

import java.util.Map;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

import com.aliasi.matrix.Vector;

/**
 * Latent semantic index
 * 
 * @author nzhiltsov
 * 
 */
public interface LSIndex {
	Map<Reference, Vector> getReferenceVectors();

	Map<String, Vector> getTermVectors();
}
