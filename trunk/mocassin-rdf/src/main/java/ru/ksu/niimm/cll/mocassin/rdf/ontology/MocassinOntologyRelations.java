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

import static java.lang.String.format;

/**
 * The relations of the Mocassin Ontology that are available for automatical
 * discovering
 * 
 * @author nzhiltsov
 * 
 */
public enum MocassinOntologyRelations {

    HAS_PART("hasPart", 0), REFERS_TO("refersTo", 1), HAS_CONSEQUENCE(
	    "hasConsequence", 2), EXEMPLIFIES("exemplifies", 3), HAS_SEGMENT(
	    "hasSegment", 4), HAS_START_PAGE_NUMBER("hasStartPageNumber", 5), HAS_TEXT(
	    "hasText", 6), HAS_TITLE("hasTitle", 7), DEPENDS_ON("dependsOn", 8), PROVES(
	    "proves", 9), FOLLOWED_BY("followedBy", 10), MENTIONS("mentions",
	    11), HAS_NOTATION("hasNotation", 12), HAS_LATEX_SOURCE("hasLatexSource", 13);
    private static final String PREFIX = "http://cll.niimm.ksu.ru/ontologies/mocassin#";
    
    private String uri;
    /**
     * surrogate unique integer code
     */
    private int code;

    private MocassinOntologyRelations(String name, int code) {
	this.uri = format("%s%s", PREFIX, name);
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
