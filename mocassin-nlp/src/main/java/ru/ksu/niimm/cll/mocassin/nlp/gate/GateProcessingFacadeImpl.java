package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.persist.PersistenceException;
import gate.security.SecurityException;

import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;

class GateProcessingFacadeImpl implements GateProcessingFacade {
	@Inject
	private Logger logger;

	private final AnnieControllerProvider<SerialAnalyserController> annieControllerProvider;

	private final GateDocumentDAO gateDocumentDAO;

	@Inject
	GateProcessingFacadeImpl(
			AnnieControllerProvider<SerialAnalyserController> annieControllerProvider,
			GateDocumentDAO gateDocumentDAO) {
		this.annieControllerProvider = annieControllerProvider;
		this.gateDocumentDAO = gateDocumentDAO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(String arxivId) throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		Document document = gateDocumentDAO.load(StringUtil
				.arxivid2gateid(arxivId));
		try {
			SerialAnalyserController annieController = annieControllerProvider
					.get();
			Corpus corpus = Factory.newCorpus("temp");
			corpus.add(document);
			annieController.setCorpus(corpus);
			annieController.execute();
			document.getDataStore().sync(document);
		} catch (ExecutionException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"failed to execute ANNIE controller over a document with id='%s'",
							arxivId));
			throw new ProcessException(e);
		} catch (PersistenceException e) {
			logger.log(Level.SEVERE, String.format(
					"failed to update the document with id='%s'", arxivId));
			throw new ProcessException(e);
		} catch (SecurityException e) {
			logger.log(Level.SEVERE, String.format(
					"failed to update the document with id='%s'", arxivId));
			throw new ProcessException(e);
		} catch (AnnieControllerCreationException e) {
			logger.log(Level.SEVERE, "failed to create the ANNIE controller");
			throw new ProcessException(e);
		} catch (ResourceInstantiationException e) {
			logger.log(Level.SEVERE, "failed to create a temporary corpus");
			throw new ProcessException(e);
		}
	}
}
