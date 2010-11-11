package ru.ksu.niimm.cll.mocassin.analyzer.relation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mocassin Ontology classes with their synonyms
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyClasses {
	AXIOM("axiom"), CLAIM("claim", "assertion", "statement"), CONJECTURE(
			"conjecture", "hypothesis"), COROLLARY("corollary"), DEFINITION(
			"definition"), EQUATION("equation", "equationgroup"), EXAMPLE(
			"example"), LEMMA("lemma"), PROOF("proof"), PROPOSITION(
			"proposition"), REMARK("remark", "note", "comment"), SECTION(
			"section", "subsection"), THEOREM("theorem");

	private String[] labels;

	private MocassinOntologyClasses(String... labels) {
		this.labels = labels;
	}

	public String[] getLabels() {
		return labels;
	}

	public static Set<String> toSet() {
		Set<String> typeSet = new HashSet<String>();
		for (MocassinOntologyClasses clazz : MocassinOntologyClasses.values()) {
			typeSet.addAll(Arrays.asList(clazz.getLabels()));
		}
		return typeSet;
	}

	public static MocassinOntologyClasses fromString(String label) {
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
