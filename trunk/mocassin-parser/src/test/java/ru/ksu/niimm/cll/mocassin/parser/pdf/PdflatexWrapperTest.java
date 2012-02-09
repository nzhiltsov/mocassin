package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

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

	@Test
	public void testCompilePatched() throws PdflatexCompilationException,
			IOException {
		String collectionId = "ivm18";
		pdflatexWrapper.compilePatched(collectionId);
		checkIfPDFGenerated(collectionId, 7);
		checkIfPdfsyncGenerated(collectionId, SEVENTH_PAGE_MARK);

		collectionId = "ivm537";
		pdflatexWrapper.compilePatched(collectionId);
		checkIfPDFGenerated(collectionId, 5);
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

	private static void checkIfPDFGenerated(String collectionId,
			int expectedNumberOfPages) throws FileNotFoundException,
			IOException {
		File pdfFile = new File("/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(collectionId, "pdf"));
		Assert.assertTrue(String.format(
				"The PDF file='%s' hasn't been generated.", pdfFile.getName()),
				pdfFile.exists());
		PDDocument pdfDoc = PDDocument.load(new FileInputStream(pdfFile));
		try {
			Assert.assertEquals(
					"Number of pages in the generated document does not equal to the expected one",
					expectedNumberOfPages, pdfDoc.getNumberOfPages());
		} finally {
			pdfDoc.close();
		}
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
