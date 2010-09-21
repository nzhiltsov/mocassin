package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

/**
 * Reference between structural elements which semantics needs to be defined
 * 
 * @author nzhiltsov
 * 
 */
public interface Reference {
	String getId();

	StructuralElement getFrom();

	StructuralElement getTo();

	List<String> getSentenceTokens();
}
