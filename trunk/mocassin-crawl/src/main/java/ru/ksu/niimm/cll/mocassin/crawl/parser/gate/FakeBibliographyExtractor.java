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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ru.ksu.niimm.cll.mocassin.util.Pair;
import ru.ksu.niimm.cll.mocassin.util.Triple;

import com.csvreader.CsvReader;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class FakeBibliographyExtractor implements BibliographyExtractor {
	private final Map<Pair<String, String>, Integer> link2number = Maps
			.newHashMap();

	@Inject
	public FakeBibliographyExtractor() throws NumberFormatException, IOException {
		CsvReader reader = new CsvReader(
				new InputStreamReader(this.getClass().getClassLoader()
						.getResourceAsStream("mathnet_citations.csv")), ';');
		reader.setTrimWhitespace(true);
		try {
			while (reader.readRecord()) {
				String fromKey = reader.get(0);
				String toKey = reader.get(1);
				int number = Integer.parseInt(reader.get(2));
				link2number.put(new Pair<String, String>(fromKey, toKey),
						number);
			}
		} finally {
			reader.close();
		}
	}

	@Override
	public Integer getNumber(String fromKey, String toKey) {
		return link2number.get(new Pair<String, String>(fromKey, toKey));
	}

	@Override
	public String getToKey(String fromKey, int number) {
		Entry<Pair<String, String>, Integer> entry = Iterables.find(
				link2number.entrySet(), new FromKeyPredicate(fromKey, number),
				null);
		return entry != null ? entry.getKey().getSecond() : null;
	}

	private static class FromKeyPredicate implements
			Predicate<Entry<Pair<String, String>, Integer>> {
		private final String key;

		private final int number;

		public FromKeyPredicate(String key, int number) {
			this.key = key;
			this.number = number;
		}

		@Override
		public boolean apply(Entry<Pair<String, String>, Integer> input) {
			return input.getKey().getFirst().equals(key)
					&& input.getValue() == number;
		}

	}
}
