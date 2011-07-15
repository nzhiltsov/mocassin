package unittest.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.nlp.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.ose.ontology.OntologyModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, OntologyModule.class, VirtuosoModule.class,
		LatexParserModule.class, FullTextModule.class })
public class ExtractMetadataUtil {
	@Inject
	private GateDocumentDAO gateDocumentDAO;
	@Inject
	private Logger logger;

	@Test
	public void testExtractAndSaveMetadata()
			throws AccessGateDocumentException, IOException {
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
				logger.log(Level.SEVERE, String.format(
						"failed to process document '%s' due to",
						e.getMessage()));
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
