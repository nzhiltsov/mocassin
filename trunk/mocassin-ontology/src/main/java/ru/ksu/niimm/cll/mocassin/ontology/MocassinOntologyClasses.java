package ru.ksu.niimm.cll.mocassin.ontology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Mocassin Ontology classes with their synonyms and classes imported from SALT
 * ontology
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyClasses {
	AXIOM("axiom"), CLAIM("claim", "assertion", "statement"), CONJECTURE(
			"conjecture", "hypothesis"), COROLLARY("corollary"), DEFINITION(
			"definition"), EQUATION("equation", "equationgroup"), EXAMPLE(
			"example"), FIGURE("figure"), LEMMA("lemma"), PROOF("proof"), PROPOSITION(
			"proposition"), REMARK("remark", "note", "comment"), SECTION(
			"section", "subsection"), THEOREM("theorem");

	private String[] labels;

	private static ImmutableMap<MocassinOntologyClasses, String> class2Uri = ImmutableMap
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
			.put(EXAMPLE, "http://cll.niimm.ksu.ru/ontologies/mocassin#Example")
			.put(FIGURE,
					"http://salt.semanticauthoring.org/ontologies/sdo#Figure")
			.put(LEMMA, "http://cll.niimm.ksu.ru/ontologies/mocassin#Lemma")
			.put(PROOF, "http://cll.niimm.ksu.ru/ontologies/mocassin#Proof")
			.put(PROPOSITION,
					"http://cll.niimm.ksu.ru/ontologies/mocassin#Proposition")
			.put(REMARK, "http://cll.niimm.ksu.ru/ontologies/mocassin#Remark")
			.put(SECTION,
					"http://salt.semanticauthoring.org/ontologies/sdo#Section")
			.put(THEOREM, "http://cll.niimm.ksu.ru/ontologies/mocassin#Theorem")
			.build();

	private MocassinOntologyClasses(String... labels) {
		this.labels = labels;
	}

	public String[] getLabels() {
		return labels;
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
