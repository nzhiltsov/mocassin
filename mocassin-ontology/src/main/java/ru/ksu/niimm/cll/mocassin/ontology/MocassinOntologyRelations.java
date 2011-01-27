package ru.ksu.niimm.cll.mocassin.ontology;

/**
 * The relations of the Mocassin Ontology that are available for automatical
 * discovering
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyRelations {
	HAS_CONSEQUENCE, EXEMPLIFIES;

	public static MocassinOntologyClasses[] getValidRanges(
			MocassinOntologyRelations relation) {
		switch (relation) {
		case HAS_CONSEQUENCE: {
			MocassinOntologyClasses[] domains = { MocassinOntologyClasses.COROLLARY };
			return domains;
		}

		case EXEMPLIFIES: {
			MocassinOntologyClasses[] domains = {
					MocassinOntologyClasses.AXIOM,
					MocassinOntologyClasses.CLAIM,
					MocassinOntologyClasses.CONJECTURE,
					MocassinOntologyClasses.COROLLARY,
					MocassinOntologyClasses.DEFINITION,
					MocassinOntologyClasses.EQUATION,
					MocassinOntologyClasses.LEMMA,
					MocassinOntologyClasses.PROPOSITION,
					MocassinOntologyClasses.REMARK,
					MocassinOntologyClasses.THEOREM };
			return domains;
		}
		default:
			throw new UnsupportedOperationException(
					"this relation isn't supported:" + relation);
		}
	}

	public static MocassinOntologyClasses[] getValidDomains(
			MocassinOntologyRelations relation) {

		switch (relation) {
		case HAS_CONSEQUENCE: {
			MocassinOntologyClasses[] domains = {
					MocassinOntologyClasses.AXIOM,
					MocassinOntologyClasses.CLAIM,
					MocassinOntologyClasses.CONJECTURE,
					MocassinOntologyClasses.COROLLARY,
					MocassinOntologyClasses.LEMMA,
					MocassinOntologyClasses.PROPOSITION,
					MocassinOntologyClasses.THEOREM };
			return domains;
		}

		case EXEMPLIFIES: {
			MocassinOntologyClasses[] domains = { MocassinOntologyClasses.EXAMPLE };
			return domains;
		}
		default:
			throw new UnsupportedOperationException(
					"this relation isn't supported:" + relation);
		}

	}
}
