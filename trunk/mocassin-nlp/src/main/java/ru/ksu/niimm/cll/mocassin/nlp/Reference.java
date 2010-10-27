package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

/**
 * Reference between structural elements which semantics need to be defined
 * 
 * @author nzhiltsov
 * 
 */
public interface Reference {
	/**
	 * 
	 * @return document where a reference is located
	 */
	ParsedDocument getDocument();

	/**
	 * 
	 * @return within-document identifier of a reference
	 */
	int getId();

	StructuralElement getFrom();

	StructuralElement getTo();

	List<Token> getSentenceTokens();

	void setSentenceTokens(List<Token> sentenceTokens);

	String getAdditionalRefid();
}
