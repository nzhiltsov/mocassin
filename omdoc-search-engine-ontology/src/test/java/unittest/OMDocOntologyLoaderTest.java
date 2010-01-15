package unittest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntProperty;

import ru.ksu.niimm.ose.ontology.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.impl.OMDocOntologyLoaderImpl;

public class OMDocOntologyLoaderTest {
	private OMDocOntologyLoader loader;

	@Before
	public void setup() {
		loader = new OMDocOntologyLoaderImpl();
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
				"http://omdoc.org/ontology#hasProperty", "has property");
		List<OntologyConcept> ontPropertyRangeList = getLoader()
				.getOntPropertyRangeList(relation);
		Assert.assertTrue(ontPropertyRangeList.size() == 1);
		Assert.assertEquals(ontPropertyRangeList.get(0).getUri(),
				"http://omdoc.org/ontology#Property");
	}

	public OMDocOntologyLoader getLoader() {
		return loader;
	}

}
