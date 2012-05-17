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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.util.OntologyConceptComparator;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyTestModule.class})
public class MocassinOntologyFacadeTest {
	@Inject
	private OntologyFacade ontologyFacade;

	@Test
	public void testGetOntClasses() {
		List<OntologyConcept> ontClassList = this.ontologyFacade
				.getOntClassList();
		Collections.sort(ontClassList, new OntologyConceptComparator());
		for (OntologyConcept concept : ontClassList) {
			System.out.println(concept);
		}
	}

	@Test
	public void testGetPropertiesForConcreteDomain() {
		OntologyConcept concept = new OntologyConcept(
				"http://cll.niimm.ksu.ru/ontologies/mocassin#Theorem",
				"theorem");
		List<OntologyRelation> properties = this.ontologyFacade
				.getOntPropertyList(concept);
		OntologyRelation relation = new OntologyRelation(
				"http://cll.niimm.ksu.ru/ontologies/mocassin#refersTo",
				"refers to");
		Assert.assertTrue(properties.contains(relation));
		relation = new OntologyRelation(
				"http://cll.niimm.ksu.ru/ontologies/mocassin#hasText",
				"has text");
		Assert.assertTrue(properties.contains(relation));
	}

	@Test
	public void testGetRangeOfConcreteProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://cll.niimm.ksu.ru/ontologies/mocassin#refersTo",
				"refers to");
		List<OntologyConcept> ontPropertyRangeList = this.ontologyFacade
				.getOntPropertyRangeList(relation);
		boolean containsConjecture = false;
		boolean containsCorollary = false;
		for (OntologyConcept rangeConcept : ontPropertyRangeList) {
			if (rangeConcept.getUri().equals(
					"http://cll.niimm.ksu.ru/ontologies/mocassin#Conjecture")) {
				containsConjecture = true;
			} else if (rangeConcept.getUri().equals(
					"http://cll.niimm.ksu.ru/ontologies/mocassin#Corollary")) {
				containsCorollary = true;
			}
		}

		Assert.assertTrue(containsConjecture && containsCorollary);
	}

	@Test
	public void testGetInferredRangeOfProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://cll.niimm.ksu.ru/ontologies/mocassin#hasSegment",
				"has segment");
		List<OntologyConcept> ontPropertyRangeList = this.ontologyFacade
				.getOntPropertyRangeList(relation);
		boolean containsDefinition = false;
		for (OntologyConcept rangeConcept : ontPropertyRangeList) {
			if (rangeConcept.getUri().equals(
					"http://cll.niimm.ksu.ru/ontologies/mocassin#Definition")) {
				containsDefinition = true;
				break;
			}
		}

		Assert.assertTrue(containsDefinition);
	}

	@Test
	public void testGetMostSpecific() {

		List<MocassinOntologyClasses> hierarchy = new ArrayList<MocassinOntologyClasses>();
		hierarchy.add(MocassinOntologyClasses.UNRECOGNIZED_DOCUMENT_SEGMENT);
		hierarchy.add(MocassinOntologyClasses.SECTION);
		MocassinOntologyClasses specificClass = this.ontologyFacade
				.getMostSpecific(hierarchy);
		Assert.assertEquals(MocassinOntologyClasses.SECTION, specificClass);
	}

    @Test
    public void testValidRelations() {
        List<MocassinOntologyRelations> relations = new ArrayList<MocassinOntologyRelations>();
        relations.add(MocassinOntologyRelations.DEPENDS_ON);
        relations.add(MocassinOntologyRelations.HAS_PART);
        relations.add(MocassinOntologyRelations.FOLLOWED_BY);
        relations.add(MocassinOntologyRelations.REFERS_TO);

        List<MocassinOntologyRelations> testRelations =
                this.ontologyFacade.getRelations(MocassinOntologyClasses.THEOREM,
                        MocassinOntologyClasses.PROPOSITION);

        Collections.sort(testRelations);
        Collections.sort(relations);

        Assert.assertEquals("The list of valid relations between two " +
                "classes is not equal to the expected one", relations, testRelations);

    }
}
