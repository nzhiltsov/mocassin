package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.csvreader.CsvReader;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class LatexDocumentHeaderPatcherTest {
	@Inject
	Logger logger;

	private static final Pattern PDFSYNC_PATTERN = Pattern
			.compile("\\\\usepackage\\{pdfsync\\}");
	private static final Pattern XCOLOR_PATTERN = Pattern
			.compile("\\\\usepackage\\{xcolor\\}");
	private static final Pattern FRAMED_PATTERN = Pattern
			.compile("\\\\usepackage\\{framed\\}");
	private static final Pattern COLORLET_PATTERN = Pattern
			.compile("\\\\colorlet\\{shadecolor\\}\\{yellow!30\\}");
	@Inject
	private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;

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

	@Test
	public void testPatch() throws FileNotFoundException {
		String collectionId = "ivm18";
		this.latexDocumentHeaderPatcher.patch(collectionId);
		checkIfFound(collectionId);
		collectionId = "ivm537";
		this.latexDocumentHeaderPatcher.patch(collectionId);
		checkIfFound(collectionId);
	}

	private void checkIfFound(final String arxivId)
			throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("/opt/mocassin/patched-tex/"
				+ StringUtil.arxivid2filename(arxivId, "tex")), "cp866");
		boolean found = false;
		boolean foundPdfsync = false;
		boolean foundXcolor = false;
		boolean foundFramed = false;
		boolean foundColorlet = false;
		while (scanner.hasNext() && !found) {
			String line = scanner.next();
			if (!foundPdfsync) {
				foundPdfsync = PDFSYNC_PATTERN.matcher(line).find();
			}
			if (!foundXcolor) {
				foundXcolor = XCOLOR_PATTERN.matcher(line).find();
			}
			if (!foundFramed) {
				foundFramed = FRAMED_PATTERN.matcher(line).find();
			}
			if (!foundColorlet) {
				foundColorlet = COLORLET_PATTERN.matcher(line).find();
			}
			found = foundPdfsync && foundXcolor && foundFramed && foundColorlet;
		}
		scanner.close();
		Assert.assertTrue(found);
	}

	@Test @Ignore
	public void testPatchMathnetArticles() {
		for (String mathnetKey : id2filename.keySet()) {
			try {
				latexDocumentHeaderPatcher.patch(mathnetKey);
			} catch (Exception e) {
				logger.log(Level.SEVERE,
						"failed to patch an article with key: " + mathnetKey);
			}
		}
	}
}
