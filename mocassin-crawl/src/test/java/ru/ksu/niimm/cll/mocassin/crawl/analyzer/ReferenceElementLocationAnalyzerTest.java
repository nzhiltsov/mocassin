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

import java.io.FileWriter;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.location.ReferenceElementLocationInfo;

import com.google.inject.Inject;
@Ignore("references should be read from a store")	
public class ReferenceElementLocationAnalyzerTest extends AbstractAnalyzerTest {
	private static final String LOCATIONS_OUTPUT_FILENAME = "/tmp/refelement-distances.txt";
	@Inject
	private ReferenceElementLocationAnalyzer referenceElementLocationAnalyzer;

	@Test
	public void testAnalyze() throws IOException {
		/*Function<Reference, ReferenceElementLocationInfo> function = new Function<Reference, ReferenceElementLocationInfo>() {

			@Override
			public ReferenceElementLocationInfo apply(Reference reference) {
				return getReferenceElementLocationAnalyzer().analyze(reference);
			}
		};

		Iterable<ReferenceElementLocationInfo> locations = Iterables.transform(
				getReferences(), function);

		print(locations, LOCATIONS_OUTPUT_FILENAME);*/
	}

	public ReferenceElementLocationAnalyzer getReferenceElementLocationAnalyzer() {
		return referenceElementLocationAnalyzer;
	}

	protected void print(Iterable<ReferenceElementLocationInfo> locations,
			String outputPath) throws IOException {
		FileWriter writer = new FileWriter(outputPath);
		for (ReferenceElementLocationInfo info : locations) {
			writer.write(String
					.format("%s %f %f\n", info.getReference().toString(), info
							.getStartDistance(), info.getEndDistance()));
		}
		writer.flush();
		writer.close();
	}
}
