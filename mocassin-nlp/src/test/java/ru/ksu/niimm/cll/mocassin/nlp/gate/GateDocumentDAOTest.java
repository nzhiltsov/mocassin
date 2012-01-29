package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.Document;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class })
public class GateDocumentDAOTest {
	private static final String SECOND_DOC = "ivm537";
	private static final String FIRST_DOC = "ivm18";
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	@Test
	public void testDeleteAndSave() throws AccessGateStorageException,
			PersistenceException, AccessGateDocumentException {
		gateDocumentDAO.delete(FIRST_DOC);
		gateDocumentDAO.delete(SECOND_DOC);
		gateDocumentDAO.save(
				FIRST_DOC,
				new File(String.format("/opt/mocassin/arxmliv/%s.tex.xml",
						FIRST_DOC)), "utf8");
		gateDocumentDAO.save(
				SECOND_DOC,
				new File(String.format("/opt/mocassin/arxmliv/%s.tex.xml",
						SECOND_DOC)), "utf8");
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

	@After
	public void after() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		gateProcessingFacade.process(FIRST_DOC);
		gateProcessingFacade.process(SECOND_DOC);
	}

	public GateDocumentDAO getGateDocumentDAO() {
		return gateDocumentDAO;
	}

}
