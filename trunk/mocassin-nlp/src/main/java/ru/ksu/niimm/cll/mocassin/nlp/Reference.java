package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

/**
 * Reference between structural elements which semantics needs to be defined
 * 
 * @author nzhiltsov
 * 
 */
public interface Reference {
	int getId();

	String getDocumentName();

	StructuralElement getFrom();

	StructuralElement getTo();

	List<String> getSentenceTokens();

	void setSentenceTokens(List<String> sentenceTokens);

	String getAdditionalRefid();
}
