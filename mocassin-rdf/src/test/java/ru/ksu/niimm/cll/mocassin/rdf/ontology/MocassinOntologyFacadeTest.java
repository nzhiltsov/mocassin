package ru.ksu.niimm.cll.mocassin.rdf.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyRelation;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;
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
}
