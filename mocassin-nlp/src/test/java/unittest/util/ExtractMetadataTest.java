package unittest.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, OntologyModule.class,
		LatexParserModule.class, FullTextModule.class })
@Ignore("Probably outdated test")
public class ExtractMetadataTest {/* TODO: check if the test is required */
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@InjectLogger
	private Logger logger;

	@Test
	public void testExtractAndSaveMetadata()
			throws AccessGateDocumentException, IOException,
			AccessGateStorageException {
		List<String> documentIds = gateDocumentDAO.getDocumentIds();
		Collections.sort(documentIds);
		List<GateDocumentMetadata> metadataList = new ArrayList<GateDocumentMetadata>();
		List<String> subList = documentIds.subList(0, 300);
		for (String id : subList) {
			try {
				GateDocumentMetadata metadata = gateDocumentDAO
						.loadMetadata(id);
				metadataList.add(metadata);
			} catch (AccessGateDocumentException e) {
				logger.error("Failed to process document '{}'", id, e);
			}

		}
		save(metadataList);
	}

	private void save(List<GateDocumentMetadata> metadataList)
			throws IOException {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("document", GateDocumentMetadata.class);
		xstream.toXML(metadataList, new FileWriter(
				"/tmp/arxmliv_collection_metadata.xml"));
	}
}
