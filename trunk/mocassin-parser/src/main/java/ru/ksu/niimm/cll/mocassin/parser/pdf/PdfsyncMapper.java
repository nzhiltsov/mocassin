package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * <a href="http://itexmac.sourceforge.net/pdfsync.html">pdfsync<a/> based
 * implementation
 * 
 */
class PdfsyncMapper implements Latex2PDFMapper {
	@Inject
	private Logger logger;

	private static final String PDF_MARK = "s";

	private static final String LATEX_MARK = "l";

	private final String PDFSYNC_DIR;

	private final String AUX_PDF_PATH;

	@Inject
	public PdfsyncMapper(@Named("pdfsync.document.dir") String pdfsyncDir,
			@Named("auxiliary.pdf.document.dir") String auxPdfPath) {
		this.PDFSYNC_DIR = pdfsyncDir;
		this.AUX_PDF_PATH = auxPdfPath;
	}

	@Override
	public int getPDFPageNumber(int latexLineNumber, String arxivId) {
		String fileName = StringUtil.arxivid2filename(arxivId, "pdfsync");
		int foundPageNumber = 0;
		CsvReader reader = null;
		try {
			reader = new CsvReader(PDFSYNC_DIR + fileName, ',');
			while (reader.readRecord()) {
				int pageNumber = Integer.parseInt(reader.get(0));
				int startLineNumber = Integer.parseInt(reader.get(1));
				int endLineNumber = Integer.parseInt(reader.get(2));
				if (latexLineNumber >= startLineNumber
						&& latexLineNumber <= endLineNumber) {
					foundPageNumber = pageNumber;
					break;
				}
			}
		} catch (IOException e) {
			logger.log(
					Level.SEVERE,
					String.format(
							"failed to get page number for a line='%d' in the document='%s' due to %s; zero will be returned",
							latexLineNumber, arxivId, e.getMessage()));
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return foundPageNumber;
	}

	@Override
	public void generateSummary(String arxivId)
			throws GeneratePdfSummaryException {
		String filename = StringUtil.arxivid2filename(arxivId, "pdfsync");
		SortedSet<Page> pages = new TreeSet<Page>();

		try {
			CsvReader reader = new CsvReader(String.format("%s/%s",
					AUX_PDF_PATH, filename), ' ');
			int startLine = -1;
			int currentLine = -1;
			while (reader.readRecord()) {

				String mark = reader.get(0);
				if (mark.equals(LATEX_MARK)) {
					currentLine = Integer.parseInt(reader.get(2));
					if (startLine == -1) {
						startLine = currentLine;
					}
				}
				if (mark.equals(PDF_MARK)) {
					int pageNumber = Integer.parseInt(reader.get(1));
					Page page = new Page(pageNumber, startLine, currentLine);
					pages.add(page);
					startLine = -1;
					currentLine = -1;
				}

			}
			reader.close();
			printPages(filename, pages);
		} catch (Exception e) {
			String message = String
					.format("failed to generate the summary for a document='%s' due to: %s",
							arxivId, e.getMessage());
			logger.log(Level.SEVERE, message);
			throw new GeneratePdfSummaryException(message);
		}

	}

	private void printPages(String filename, SortedSet<Page> pages)
			throws IOException {
		CsvWriter writer = new CsvWriter(String.format("%s/%s", PDFSYNC_DIR,
				filename));
		for (Page page : pages) {
			String[] record = { String.valueOf(page.number),
					String.valueOf(page.startLine),
					String.valueOf(page.endLine) };
			writer.writeRecord(record);
		}
		writer.flush();
		writer.close();
	}

	private static class Page implements Comparable<Page> {
		/**
		 * page number in the PDF file
		 */
		int number;
		/**
		 * start line of this page in the Latex file
		 */
		int startLine;
		/**
		 * end line of this page in the Latex file
		 */
		int endLine;

		public Page(int number, int startLine, int endLine) {
			this.number = number;
			this.startLine = startLine;
			this.endLine = endLine;
		}

		@Override
		public int compareTo(Page p) {
			if (number < p.number)
				return -1;
			if (number > p.number)
				return 1;
			return 0;
		}

	}
}
