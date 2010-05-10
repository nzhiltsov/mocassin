package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.OntologyRelation;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { OntologyModule.class, VirtuosoModule.class })
public class OMDocOntologyFacadeTest {
	@Inject
	private OMDocOntologyFacade omdocOntologyFacade;

	@Test
	public void testGetOntClassList() {
		List<OntologyConcept> ontClassList = getOmdocOntologyFacade()
				.getOntClassList();
		Assert.assertTrue(ontClassList != null && ontClassList.size() > 0);
	}

	@Test
	public void testGetOntPropertyList() {
		List<OntologyConcept> ontClassList = getOmdocOntologyFacade()
				.getOntClassList();
		for (OntologyConcept concept : ontClassList) {
			getOmdocOntologyFacade().getOntPropertyList(concept);
		}

	}

	@Test
	public void testGetPropertiesForConcreteDomain() {
		OntologyConcept concept = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		List<OntologyRelation> properties = getOmdocOntologyFacade()
				.getOntPropertyList(concept);
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "uses symbol");
		Assert.assertTrue(properties.contains(relation));
	}

	@Test
	public void testGetOntPropertyRangeList() {
		List<OntologyConcept> ontClassList = getOmdocOntologyFacade()
				.getOntClassList();
		for (OntologyConcept concept : ontClassList) {
			List<OntologyRelation> ontPropertyList = getOmdocOntologyFacade()
					.getOntPropertyList(concept);
			for (OntologyRelation relation : ontPropertyList) {
				getOmdocOntologyFacade().getOntPropertyRangeList(relation);
			}
		}

	}

	@Test
	public void testGetRangeOfConcreteProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#proves", "proves");
		List<OntologyConcept> ontPropertyRangeList = getOmdocOntologyFacade()
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
		List<OntologyConcept> ontPropertyRangeList = getOmdocOntologyFacade()
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

	public OMDocOntologyFacade getOmdocOntologyFacade() {
		return omdocOntologyFacade;
	}

}
