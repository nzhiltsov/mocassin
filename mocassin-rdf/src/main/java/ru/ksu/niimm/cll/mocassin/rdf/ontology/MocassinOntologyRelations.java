package ru.ksu.niimm.cll.mocassin.rdf.ontology;

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
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasStartPageNumber", 5), HAS_TEXT(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasText", 6), HAS_TITLE(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#hasTitle", 7), DEPENDS_ON(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#dependsOn", 8), PROVES(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#proves", 9), FOLLOWED_BY(
			"http://cll.niimm.ksu.ru/ontologies/mocassin#followedBy", 10);

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
				String.format(
						"couldn't find Mocassin Ontology relation with a given URI: %s",
						uri));
	}

	public static MocassinOntologyClasses[] getValidRanges(
			MocassinOntologyRelations relation) {
		switch (relation) {
		case PROVES: {
			MocassinOntologyClasses[] domains = {
					MocassinOntologyClasses.CLAIM,
					MocassinOntologyClasses.COROLLARY,
					MocassinOntologyClasses.LEMMA,
					MocassinOntologyClasses.PROPOSITION,
					MocassinOntologyClasses.THEOREM };
			return domains;
		}
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
		case PROVES: {
			MocassinOntologyClasses[] domains = { MocassinOntologyClasses.PROOF };
			return domains;
		}
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

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

}