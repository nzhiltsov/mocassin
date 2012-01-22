package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Scanner;
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
	private static final String FIFTH_PAGE_MARK = "s 5";
	private static final String SEVENTH_PAGE_MARK = "s 7";
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
	public void testCompilePatched() throws PdflatexCompilationException,
			IOException {
		String collectionId = "ivm18";
		pdflatexWrapper.compilePatched(collectionId);
		checkIfPDFGenerated(collectionId);
		checkIfPdfsyncGenerated(collectionId, SEVENTH_PAGE_MARK);

		collectionId = "ivm537";
		pdflatexWrapper.compilePatched(collectionId);
		checkIfPDFGenerated(collectionId);
		checkIfPdfsyncGenerated(collectionId, FIFTH_PAGE_MARK);
	}

	private static void checkIfPdfsyncGenerated(String collectionId, String mark)
			throws IOException {
		File pdfsyncFile = new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(collectionId, "pdfsync"));
		Assert.assertTrue(String.format(
				"The pdfsync file='%s' hasn't been generated.",
				pdfsyncFile.getName()), pdfsyncFile.exists());
		checkIfFound(pdfsyncFile, mark);
	}

	private static void checkIfPDFGenerated(String collectionId) {
		File pdfFile = new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(collectionId, "pdf"));
		Assert.assertTrue(String.format(
				"The PDF file='%s' hasn't been generated.", pdfFile.getName()),
				pdfFile.exists());
	}

	private static void checkIfFound(File file, String mark) throws IOException {
		boolean found = false;
		LineNumberReader lineReader = new LineNumberReader(new FileReader(file));
		try {

			String line = null;
			while ((line = lineReader.readLine()) != null) {
				found = line.contains(mark);
				if (found) {
					break;
				}
			}
		} finally {
			lineReader.close();
		}
		Assert.assertTrue(
				String.format(
						"The necessary page mark='%s' for the file='%s' hasn't been found.",
						mark, file.getName()), found);
	}
}
