package unittest;

import gate.Document;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;

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

	@Test
	public void testFindById() throws Exception {
		List<String> ids = getGateDocumentDAO().getDocumentIds();
		String foundId = null;
		for (String id : ids) {
			if (id.startsWith("f000008.tex")) {
				foundId = id;
			}
		}
		Assert.assertNotNull(foundId);
		Document document = getGateDocumentDAO().load(foundId);
		StructuralElement foundElement = getStructuralElementSearcher()
				.findById(document, 3747);
		getGateDocumentDAO().release(document);
		System.out.println(foundElement);
	}

	public StructuralElementSearcher getStructuralElementSearcher() {
		return structuralElementSearcher;
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
