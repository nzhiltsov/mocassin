package unittest;

import gate.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
public class ReferenceSearcherTest {
	@Inject
	private Logger logger;
	@Inject
	private ReferenceSearcher referenceSearcher;

	@Inject
	private GateDocumentDAO gateDocumentDAO;

	@Test
	public void testRetrieve() throws AccessGateDocumentException, IOException {
		List<DocumentData> data = new ArrayList<DocumentData>();
		List<String> ids = CollectionUtil.sampleRandomSublist(gateDocumentDAO
				.getDocumentIds(), 30);
		for (String id : ids) {
			Document document = null;
			try {
				document = gateDocumentDAO.load(id);
				List<Reference> references = referenceSearcher
						.retrieve(document);
				data.add(new DocumentData(document.getName(), document
						.getContent().size(), references.size()));
				logger.log(Level.INFO, String.format(
						"the document '%s' was processed successfully", id));
			} catch (AccessGateDocumentException e) {
				logger.log(Level.SEVERE, String.format(
						"failed to load the document: %s", id));
			} catch (RuntimeException ex) {
				logger.log(Level.SEVERE, String.format(
						"failed to retrieve references from the document: %s",
						id));
			} finally {
				if (document != null) {
					gateDocumentDAO.release(document);
					document = null;
				}
			}

		}
		printDocs(data);
	}

	private void printDocs(List<DocumentData> docs) throws IOException {
		FileWriter writer = new FileWriter(new File("/tmp/reference-stats.txt"));
		try {
			writer.write("filename size count\n");
			for (DocumentData doc : docs) {
				writer.write(String.format("%s %s %s\n", doc.filename,
						doc.size, doc.referenceCount));
			}
		} finally {
			writer.flush();
			writer.close();
		}
	}

	class DocumentData {
		String filename;
		long size;
		int referenceCount;

		DocumentData(String filename, long size, int referenceCount) {
			this.filename = filename;
			this.size = size;
			this.referenceCount = referenceCount;
		}

	}
}
