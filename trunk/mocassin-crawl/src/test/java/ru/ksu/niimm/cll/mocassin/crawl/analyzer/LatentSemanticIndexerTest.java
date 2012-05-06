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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.LatentSemanticIndexer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.lsa.impl.LSIndex;

import com.google.inject.Inject;
@Ignore("references should be read from a store")	
public class LatentSemanticIndexerTest extends AbstractAnalyzerTest {

	private static final String TERM_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-terms.txt";

	private static final String REF_VECTORS_OUTPUT_FILENAME = "/tmp/lsi-refcontexts.txt";

	@Inject
	private LatentSemanticIndexer latentSemanticIndexer;

	@Test
	public void testReferenceBuildIndex() throws IOException {
		LSIndex index = getLatentSemanticIndexer().buildReferenceIndex(
				getReferences());
		print(index.getTermVectors(), TERM_VECTORS_OUTPUT_FILENAME, null);
		print(index.getReferenceVectors(), REF_VECTORS_OUTPUT_FILENAME, null);
	}

	public LatentSemanticIndexer getLatentSemanticIndexer() {
		return latentSemanticIndexer;
	}

}
