package unittest;

import gate.Document;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class StructuralElementSearcherTest {
	@Inject
	private StructuralElementSearcher structuralElementSearcher;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	private Document document;

	@Before
	public void init() throws Exception {
		List<String> ids = getGateDocumentDAO().getDocumentIds();
		String foundId = null;
		for (String id : ids) {
			if (id.startsWith("f000022.tex")) {
				foundId = id;
			}
		}
		Assert.assertNotNull(foundId);
		document = getGateDocumentDAO().load(foundId);
	}

	@Test
	public void testFindById() throws Exception {

		StructuralElement foundElement = getStructuralElementSearcher()
				.findById(document, 11808);
		getGateDocumentDAO().release(document);
		System.out.println(foundElement);
	}

	@Test
	public void testFindClosestPredecessor() {
		MocassinOntologyClasses[] hasConsequenceDomains = MocassinOntologyRelations
				.getValidDomains(MocassinOntologyRelations.HAS_CONSEQUENCE);
		StructuralElement predecessor = getStructuralElementSearcher()
				.findClosestPredecessor(document, 11808, hasConsequenceDomains);
		System.out.println(predecessor);
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
