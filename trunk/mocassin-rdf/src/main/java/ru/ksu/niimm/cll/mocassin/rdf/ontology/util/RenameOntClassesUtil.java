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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.util;

import static java.lang.String.format;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Pattern;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class RenameOntClassesUtil {
	private static final String OWL_THING_URI = "http://www.w3.org/2002/07/owl#Thing";
	private static final Pattern SMALL_RUSSIAN_START_LETTER_PATTERN = Pattern
			.compile("^[а-я].*");
	private static final String RU_LOCALE = "ru";
	private static final String EN_LOCALE = "en";

	private static Map<String, String> processedURIs = new HashMap<String, String>();

	public static String refactor(OntModel model) {

		int i = 1;
		Stack<OntClass> stack = new Stack<OntClass>();
		stack.push(model.getOntClass(OWL_THING_URI));
		while (!stack.isEmpty()) {
			OntClass clazz = stack.pop();
			if (!clazz.getURI().equals(OWL_THING_URI)
					&& !processedURIs.containsKey(clazz.getURI())) {
				processedURIs.put(clazz.getURI(),
						format("%sE%d", clazz.getNameSpace(), i));
				i++;
			}
			List<OntClass> children = clazz.listSubClasses(true).toList();
			Collections.sort(children, new OntClassByLabelComparatorDesc());
			for (OntClass child : children) {
				stack.push(child);
			}
		}

		StringWriter sw = new StringWriter();
		model.write(sw, "RDF/XML");
		String modelAsString = sw.toString();
		for (Entry<String, String> entry : processedURIs.entrySet()) {
			modelAsString = modelAsString.replace(
					format("\"%s\"", entry.getKey()),
					format("\"%s\"", entry.getValue()));
		}
		return modelAsString;
	}

	private static String prepareLabel(OntClass ontClass) {
		String rdfsLabel = ontClass.getLabel(RU_LOCALE) != null ? ontClass
				.getLabel(RU_LOCALE) : ontClass.getLabel(EN_LOCALE);
		rdfsLabel = rdfsLabel != null ? rdfsLabel : ontClass.getURI()
				.substring(ontClass.getURI().indexOf("#") + 1);
		if (SMALL_RUSSIAN_START_LETTER_PATTERN.matcher(rdfsLabel).matches()) {
			StringBuilder sb = new StringBuilder();
			sb.append(Character.toUpperCase(rdfsLabel.charAt(0)));
			sb.append(rdfsLabel.substring(1));
			rdfsLabel = sb.toString();
		}

		String preparedLabel = rdfsLabel.replace("_", " ");
		return preparedLabel;
	}

	private static class OntClassByLabelComparatorDesc implements
			Comparator<OntClass> {

		@Override
		public int compare(OntClass first, OntClass second) {
			return prepareLabel(second).compareTo(prepareLabel(first));
		}

	}

	private static class StringLengthComparator implements Comparator<String> {

		@Override
		public int compare(String first, String second) {
			if (first.length() > second.length()) {
				return -1;
			} else if (first.length() < second.length()) {
				return 1;
			}
			return 0;
		}

	}
}
