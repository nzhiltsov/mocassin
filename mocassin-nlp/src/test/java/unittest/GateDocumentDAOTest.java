package unittest;

import gate.Document;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.gate.impl.PersistenceException;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class GateDocumentDAOTest {
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	@Ignore
	@Test
	public void testSave() throws AccessGateStorageException,
			PersistenceException {
		gateDocumentDAO.save("math/0002188", new File(
				"/opt/mocassin/arxmliv/math_0002188.tex.xml"));
		gateDocumentDAO.save("math/0001036", new File(
		"/opt/mocassin/arxmliv/math_0001036.tex.xml"));
	}

	@Test
	public void testLoad() throws Exception {
		List<String> ids = CollectionUtil.sampleRandomSublist(
				getGateDocumentDAO().getDocumentIds(), 10);
		for (String id : ids) {
			Document document = null;
			try {
				document = getGateDocumentDAO().load(id);
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						String.format("couldn't load the document: %s", id));
			} finally {
				if (document != null) {
					logger.log(Level.INFO,
							String.format("document %s is OK", id));
					getGateDocumentDAO().release(document);
					document = null;
				}
			}

		}
	}

	@Test
	public void testLoadMetadata() throws AccessGateDocumentException,
			AccessGateStorageException {
		GateDocumentMetadata metadata = gateDocumentDAO
				.loadMetadata("math_0410002");
		Assert.assertEquals("math_0410002.tex.xml", metadata.getName());
		Assert.assertEquals("On certain multiplicity one theorems",
				metadata.getTitle());
		Assert.assertEquals(2, metadata.getAuthorNames().size());
		Assert.assertEquals("Jeffrey D Adler", metadata.getAuthorNames().get(0));
		Assert.assertEquals("Dipendra Prasad School", metadata.getAuthorNames()
				.get(1));
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
