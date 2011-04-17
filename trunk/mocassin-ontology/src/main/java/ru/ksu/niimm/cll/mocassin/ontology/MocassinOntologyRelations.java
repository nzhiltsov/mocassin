package ru.ksu.niimm.cll.mocassin.ontology;

/**
 * The relations of the Mocassin Ontology that are available for automatical
 * discovering
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyRelations {

	HAS_PART("http://cll.niimm.ksu.ru/ontologies/mocassin#hasPart", 0), REFERS_TO(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#refersTo", 1), HAS_CONSEQUENCE(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasConsequence", 2), EXEMPLIFIES(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#exemplifies", 3), HAS_SEGMENT(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasSegment", 4), HAS_START_PAGE_NUMBER(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasStartPageNumber", 5);

	private String uri;
	/**
	 * surrogate unique integer code
	 */
	private int code;

	private MocassinOntologyRelations(String uri, int code) {
		this.uri = uri;
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getUri() {
		return uri;
	}

	public static MocassinOntologyRelations fromUri(String uri) {
		for (MocassinOntologyRelations rel : MocassinOntologyRelations.values()) {
			if (rel.getUri().equals(uri)) {
				return rel;
			}
		}
		throw new RuntimeException(
				String
						.format(
								"couldn't find Mocassin Ontology relation with a given URI: %s",
								uri));
	}

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
					MocassinOntologyClasses.LEMMA,
					MocassinOntologyClasses.PROPOSITION,
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
