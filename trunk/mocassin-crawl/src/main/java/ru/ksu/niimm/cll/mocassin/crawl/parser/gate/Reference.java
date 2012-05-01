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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;

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

	List<Token> getSentenceTokens();

	void setSentenceTokens(List<Token> sentenceTokens);

	String getAdditionalRefid();

	MocassinOntologyRelations getPredictedRelation();

	void setPredictedRelation(MocassinOntologyRelations relation);
}
