package ru.ksu.niimm.cll.mocassin.ontology;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableBiMap;

/**
 * Mocassin Ontology classes with their synonyms and classes imported from SALT
 * ontology
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyClasses {
	AXIOM(0, "axiom", "аксиома"), CLAIM(1, "claim", "assertion", "statement", "утверждение"), CONJECTURE(
			2, "conjecture", "hypothesis", "гипотеза"), COROLLARY(3, "corollary", "следствие"), DEFINITION(
			4, "definition", "определение"), EQUATION(5, "equation", "equationgroup"), EXAMPLE(
			6, "example", "пример"), FIGURE(7, "figure"), LEMMA(8, "lemma", "лемма"), PROOF(9,
			"proof", "доказательство"), PROPOSITION(10, "proposition", "предложение"), REMARK(11, "remark",
			"note", "comment", "замечание", "комментарий"), SECTION(12, "section", "subsection", "subsubsection"), THEOREM(
			13, "theorem", "теорема"), UNRECOGNIZED_DOCUMENT_SEGMENT(14, "");

	private String[] labels;
	/**
	 * surrogate unique integer code
	 */
	private int code;

	private MocassinOntologyClasses(int code, String... labels) {
		this.code = code;
		this.labels = labels;
	}

	private static ImmutableBiMap<MocassinOntologyClasses, String> class2Uri = ImmutableBiMap
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
			.put(UNRECOGNIZED_DOCUMENT_SEGMENT, "http://cll.niimm.ksu.ru/ontologies/mocassin#DocumentSegment")
			.build();

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
