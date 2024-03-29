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
package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableBiMap;

/**
 * Mocassin Ontology classes with their synonyms and the classes imported from
 * SALT ontology
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyClasses {
    AXIOM(0, "axiom", "аксиома"), CLAIM(1, "claim", "assertion", "statement",
	    "утверждение"), CONJECTURE(2, "conjecture", "hypothesis",
	    "гипотеза"), COROLLARY(3, "corollary", "следствие"), DEFINITION(4,
	    "definition", "определение"), EQUATION(5, "equation",
	    "equationgroup", "gather", "eqnarray", "array", "align", "alignat",
	    "split", "multline"), EXAMPLE(6, "example", "пример"), FIGURE(7,
	    "figure", "рисунок", "график", "изображение"), LEMMA(8, "lemma",
	    "лемма"), PROOF(9, "proof", "доказательство"), PROPOSITION(10,
	    "proposition", "предложение"), REMARK(11, "remark", "note",
	    "comment", "замечание", "комментарий"), SECTION(12, "section",
	    "subsection", "subsubsection"), THEOREM(13, "theorem", "теорема"), UNRECOGNIZED_DOCUMENT_SEGMENT(
	    14, ""), TABLE(15, "tabular", "table", "таблица"), FORMULA(16,
	    "formula"), VARIABLE(17, "variable");

    private String[] labels;
    /**
     * surrogate unique integer code
     */
    private int code;

    private MocassinOntologyClasses(int code, String... labels) {
	this.code = code;
	this.labels = labels;
    }

    private static ImmutableBiMap<MocassinOntologyClasses, String> class2Uri;
    static {
	class2Uri = ImmutableBiMap
		.<MocassinOntologyClasses, String> builder()
		.put(AXIOM, "http://cll.niimm.ksu.ru/ontologies/mocassin#Axiom")
		.put(CLAIM, "http://cll.niimm.ksu.ru/ontologies/mocassin#Claim")
		.put(CONJECTURE,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Conjecture")
		.put(COROLLARY,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Corollary")
		.put(DEFINITION,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Definition")
		.put(EQUATION,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Equation")
		.put(EXAMPLE,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Example")
		.put(FIGURE,
			"http://salt.semanticauthoring.org/ontologies/sdo#Figure")
		.put(LEMMA, "http://cll.niimm.ksu.ru/ontologies/mocassin#Lemma")
		.put(PROOF, "http://cll.niimm.ksu.ru/ontologies/mocassin#Proof")
		.put(PROPOSITION,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Proposition")
		.put(REMARK,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Remark")
		.put(SECTION,
			"http://salt.semanticauthoring.org/ontologies/sdo#Section")
		.put(TABLE,
			"http://salt.semanticauthoring.org/ontologies/sdo#Table")
		.put(THEOREM,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Theorem")
		.put(UNRECOGNIZED_DOCUMENT_SEGMENT,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#DocumentSegment")
		.put(FORMULA,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Formula")
		.put(VARIABLE,
			"http://cll.niimm.ksu.ru/ontologies/mocassin#Variable")
		.build();
	if (class2Uri.keySet().size() != new HashSet<MocassinOntologyClasses>(
		Arrays.asList(MocassinOntologyClasses.values())).size()) {
	    throw new IllegalStateException(
		    MocassinOntologyClasses.class.getName()
			    + " is inconsistent. See the associated map.");
	}
    }

    public String[] getLabels() {
	return labels;
    }

    public int getCode() {
	return code;
    }

    public static String getUri(MocassinOntologyClasses clazz) {
	return class2Uri.get(clazz);
    }

    public static Set<String> toSet() {
	Set<String> typeSet = new HashSet<String>();
	for (MocassinOntologyClasses clazz : MocassinOntologyClasses.values()) {
	    typeSet.addAll(Arrays.asList(clazz.getLabels()));
	}
	return typeSet;
    }

    public static MocassinOntologyClasses fromUri(String uri) {
	return class2Uri.inverse().get(uri);
    }

    public static MocassinOntologyClasses fromLabel(String label) {
	for (MocassinOntologyClasses clazz : MocassinOntologyClasses.values()) {
	    List<String> clazzLabels = Arrays.asList(clazz.getLabels());
	    if (clazzLabels.contains(label)) {
		return clazz;
	    }
	}
	throw new RuntimeException(String.format(
		"couldn't find Mocassin Ontology class for given label: %s",
		label));
    }

    @Override
    public String toString() {
	return super.toString().toLowerCase();
    }

}
