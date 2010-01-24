package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntProperty;

import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.impl.OMDocOntologyFacadeImpl;

public class OMDocOntologyLoaderTest {
	private OMDocOntologyFacade loader;

	@Before
	public void setup() {
		loader = new OMDocOntologyFacadeImpl();
	}

	@Test
	public void testGetOntClassList() {
		List<OntologyConcept> ontClassList = getLoader().getOntClassList();
		Assert.assertTrue(ontClassList != null && ontClassList.size() > 0);
	}

	@Test
	public void testGetOntPropertyList() {
		List<OntologyConcept> ontClassList = getLoader().getOntClassList();
		for (OntologyConcept concept : ontClassList) {
			getLoader().getOntPropertyList(concept);
		}

	}

	@Test
	public void testGetPropertiesForConcreteDomain() {
		OntologyConcept concept = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		List<OntologyRelation> properties = getLoader().getOntPropertyList(
				concept);
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "uses symbol");
		Assert.assertTrue(properties.contains(relation));
	}

	@Test
	public void testGetOntPropertyRangeList() {
		List<OntologyConcept> ontClassList = getLoader().getOntClassList();
		for (OntologyConcept concept : ontClassList) {
			List<OntologyRelation> ontPropertyList = getLoader()
					.getOntPropertyList(concept);
			for (OntologyRelation relation : ontPropertyList) {
				getLoader().getOntPropertyRangeList(relation);
			}
		}

	}

	@Test
	public void testGetRangeOfConcreteProperty() {
		OntologyRelation relation = new OntologyRelation(
				"http://omdoc.org/ontology#proves", "proves");
		List<OntologyConcept> ontPropertyRangeList = getLoader()
				.getOntPropertyRangeList(relation);
		boolean containsAssertion = false;
		boolean containsAssumption = false;
		for (OntologyConcept rangeConcept : ontPropertyRangeList) {
			if (rangeConcept.getUri().equals(
					"http://omdoc.org/ontology#Assertion")) {
				containsAssertion = true;
			} else if (rangeConcept.getUri().equals(
					"http://omdoc.org/ontology#Assumption")) {
				containsAssumption = true;
			}
		}

		Assert.assertTrue(containsAssertion && containsAssumption);
	}

	public OMDocOntologyFacade getLoader() {
		return loader;
	}

}
