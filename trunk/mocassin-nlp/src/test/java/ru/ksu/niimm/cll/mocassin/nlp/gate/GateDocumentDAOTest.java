package ru.ksu.niimm.cll.mocassin.nlp.gate;

import gate.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.GateDocumentMetadata;

import com.csvreader.CsvReader;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class})
public class GateDocumentDAOTest {
	@Inject
	private Logger logger;
	@Inject
	private GateDocumentDAO gateDocumentDAO;

	/**
	 * e.g. <"ivm227", "1997/04/01-4.TEX">
	 */
	private final Map<String, String> id2filename = Maps.newHashMap();

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(
				new InputStreamReader(this.getClass().getClassLoader()
						.getResourceAsStream("mathnet_izvestiya.csv")), ';');
		reader.setTrimWhitespace(true);
		try {
			while (reader.readRecord()) {
				String id = reader.get(0);
				String filename = reader.get(1);
				StringTokenizer st = new StringTokenizer(filename, "-.");
				String name = st.nextToken();
				String volume = st.nextToken();
				String year = st.nextToken();
				String extension = st.nextToken();
				String filepath = String.format("%s/%s/%s-%s.%s", year,
						volume.length() == 1 ? "0" + volume : volume, name,
						volume, extension);
				id2filename.put(id, filepath);
			}
		} finally {
			reader.close();
		}
	}

	@Ignore
	@Test
	public void testSave() throws AccessGateStorageException,
			PersistenceException {
		gateDocumentDAO.save("math/0002188", new File(
				"/opt/mocassin/arxmliv/math_0002188.tex.xml"), "utf8");
		gateDocumentDAO.save("math/0001036", new File(
				"/opt/mocassin/arxmliv/math_0001036.tex.xml"), "utf8");
	}

	@Test
	@Ignore
	public void testSaveMathnetArticles() {
		int i = 0;
		for (String mathnetKey : id2filename.keySet()) {
			try {
				File file = new File(String.format(
						"/opt/mocassin/arxmliv/%s.tex.xml", mathnetKey));
				if (file.exists()) {
					gateDocumentDAO.save(mathnetKey, file, "utf8");
					i++;
				}
			} catch (Exception e) { // nothing
			}
		}
		logger.log(Level.INFO, i + " document(s) have been saved");
	}

	@Test
	public void testLoad() throws Exception {
		List<String> ids = CollectionUtil.sampleRandomSublist(
				getGateDocumentDAO().getDocumentIds(), 5);
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
