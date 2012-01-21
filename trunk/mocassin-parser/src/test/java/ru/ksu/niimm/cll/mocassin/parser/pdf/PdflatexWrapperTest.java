package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.csvreader.CsvReader;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class PdflatexWrapperTest {
	@Inject
	Logger logger;
	@Inject
	private PdflatexWrapper pdflatexWrapper;

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
	public void testCompilePatched() throws PdflatexCompilationException {
		String arxivId = "ivm18";
		pdflatexWrapper.compilePatched(arxivId);

		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdf")).exists());
		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
		arxivId = "ivm537";
		pdflatexWrapper.compilePatched(arxivId);

		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdf")).exists());
		Assert.assertTrue(new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdfsync")).exists());
	}

	@Test
	@Ignore
	public void testCompileMathnetArticles() {
		for (String mathnetKey : id2filename.keySet()) {
			try {
				pdflatexWrapper.compilePatched(mathnetKey);
			} catch (Exception e) { // nothing
			}
		}
	}
}
