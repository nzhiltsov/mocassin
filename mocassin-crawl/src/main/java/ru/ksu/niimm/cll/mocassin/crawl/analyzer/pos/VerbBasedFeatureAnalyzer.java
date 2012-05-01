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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.pos;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.indexers.Index;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

/**
 * This indexer takes into account only verb tokens of a referential sentence
 * using information about their POS tags.
 * 
 * 
 * @author nzhiltsov
 * 
 */
public interface VerbBasedFeatureAnalyzer {

	/**
	 * returns a TF-IDF weighted vector index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	Index buildReferenceWeightedIndex(List<Reference> references);

	/**
	 * returns a boolean vector index for given collections of references
	 * 
	 * @param references
	 * @return
	 */
	Index buildReferenceBooleanIndex(List<Reference> references);
}
