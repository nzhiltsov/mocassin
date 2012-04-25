package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import gate.Document;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class })
public class GateDocumentDAOTest {
	private static final String SECOND_DOC = "ivm537";
	private static final String FIRST_DOC = "ivm18";
	@InjectLogger
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
		final int expectedLoadedDocCount = 2;
		List<String> ids = CollectionUtil.sampleRandomSublist(
				getGateDocumentDAO().getDocumentIds(), expectedLoadedDocCount);
		int loadedDocCount = 0;
		for (String id : ids) {
			Document document = null;
			try {
				document = getGateDocumentDAO().load(id);
				logger.debug("The document '{}' is OK", id);
				loadedDocCount++;
			} catch (Exception e) {
				logger.debug("Couldn't load the document: {}", id, e);
			} finally {
				getGateDocumentDAO().release(document);
			}
		}
		Assert.assertEquals(
				"Not all the document have been successfully loaded.",
				expectedLoadedDocCount, loadedDocCount);
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
