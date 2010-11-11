package ru.ksu.niimm.cll.mocassin.analyzer.relation;

/**
 * The relations of the Mocassin Ontology that are available for automatical
 * discovering
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyRelations {
	HAS_CONSEQUENCE;

	public static MocassinOntologyClasses[] getValidDomains(
			MocassinOntologyRelations relation) {
		if (relation == HAS_CONSEQUENCE) {
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
		throw new UnsupportedOperationException(String.format(
				"this relation is not supported: %s", relation.toString()));
	}
}
