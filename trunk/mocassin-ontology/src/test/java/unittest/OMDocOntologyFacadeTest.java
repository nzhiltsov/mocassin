package unittest;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyRelation;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import unittest.util.OntologyConceptComparator;
import unittest.util.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyTestModule.class, VirtuosoModule.class })
@Ignore
public class OMDocOntologyFacadeTest {
	@Inject
	private OntologyFacade omdocOntologyFacade;

	@Test
	public void testGetOntClasses() {
		List<OntologyConcept> ontClassList = getOntologyFacade()
				.getOntClassList();
		Collections.sort(ontClassList, new OntologyConceptComparator());
		for (OntologyConcept concept : ontClassList) {
			System.out.println(concept);
		}
	}

	@Test
	public void testGetPropertiesForConcreteDomain() {
		OntologyConcept concept = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		List<OntologyRelation> properties = getOntologyFacade()
				.getOntPropertyList(concept);
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "uses symbol");
		Assert.assertTrue(properties.contains(relation));
	}

	@Test
	public void testGetRangeOfConcreteProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#proves", "proves");
		List<OntologyConcept> ontPropertyRangeList = getOntologyFacade()
				.getOntPropertyRangeList(relation);
		boolean containsAssertion = false;
		boolean containsAssumption = false;
		for (OntologyConcept rangeConcept : ontPropertyRangeList) {
			if (rangeConcept.getUri().equals(
					"http://omdoc.org/ontology#Assertion")) {
				containsAssertion = true;
			} else if (rangeConcept.getUri().equals(
					"http://omdoc.org/ontology#AssumptionAssertion")) {
				containsAssumption = true;
			}
		}

		Assert.assertTrue(containsAssertion && containsAssumption);
	}

	@Test
	public void testGetInferredRangeOfProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#exemplifies", "exemplifies");
		List<OntologyConcept> ontPropertyRangeList = getOntologyFacade()
				.getOntPropertyRangeList(relation);
		boolean containsDefinition = false;
		for (OntologyConcept rangeConcept : ontPropertyRangeList) {
			if (rangeConcept.getUri().equals(
					"http://omdoc.org/ontology#Definition")) {
				containsDefinition = true;
				break;
			}
		}

		Assert.assertTrue(containsDefinition);
	}

	public OntologyFacade getOntologyFacade() {
		return omdocOntologyFacade;
	}

}
