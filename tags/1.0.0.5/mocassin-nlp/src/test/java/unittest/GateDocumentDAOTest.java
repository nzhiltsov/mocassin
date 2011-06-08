package unittest;

import gate.Document;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class GateDocumentDAOTest {
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	@Test
	public void testLoad() throws Exception {
		List<String> ids = CollectionUtil.sampleRandomSublist(
				getGateDocumentDAO().getDocumentIds(), 10);
		for (String id : ids) {
			Document document = null;
			try {
				document = getGateDocumentDAO().load(id);
			} catch (Exception e) {
				logger.log(Level.SEVERE, String.format(
						"couldn't load the document: %s", id));
			} finally {
				if (document != null) {
					logger.log(Level.INFO, String.format("document %s is OK",
							id));
					getGateDocumentDAO().release(document);
					document = null;
				}
			}

		}
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
