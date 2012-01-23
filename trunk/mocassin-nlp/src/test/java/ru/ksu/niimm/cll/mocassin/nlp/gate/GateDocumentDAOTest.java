package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.Document;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class })
public class GateDocumentDAOTest {
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	@Test
	public void testDeleteAndSave() throws AccessGateStorageException,
			PersistenceException, AccessGateDocumentException {
		gateDocumentDAO.delete("ivm18");
		gateDocumentDAO.delete("ivm537");
		gateDocumentDAO.save("ivm18", new File(
				"/opt/mocassin/arxmliv/ivm18.tex.xml"), "utf8");
		gateDocumentDAO.save("ivm537", new File(
				"/opt/mocassin/arxmliv/ivm537.tex.xml"), "utf8");
	}

	@Test
	public void testLoad() throws Exception {
		List<String> ids = CollectionUtil.sampleRandomSublist(
				getGateDocumentDAO().getDocumentIds(), 2);
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
				}
			}

		}
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
