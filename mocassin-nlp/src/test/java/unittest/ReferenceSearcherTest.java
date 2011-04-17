package unittest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(NlpModule.class)
@Ignore("needs to fix the performance of the test")
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
			ParsedDocument document = new ParsedDocumentImpl(id);
			try {

				List<Reference> references = referenceSearcher
						.retrieveReferences(document);
				if (!references.isEmpty()) {
					String filename = references.get(0).getDocument()
							.getFilename();
					long size = references.get(0).getDocument().getSize();
					data
							.add(new DocumentData(filename, size, references
									.size()));
				}
				logger.log(Level.INFO, String.format(
						"the document '%s' was processed successfully", id));
			} catch (RuntimeException ex) {
				logger.log(Level.SEVERE, String.format(
						"failed to retrieve references from the document: %s",
						id));

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

	static class DocumentData {
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
