/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

import static java.lang.String.format;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

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
	@InjectLogger
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
			logger.warn(
					"Failed to get page number for a line={}; zero will be returned",
					format("'%d' in the document='%s'", latexLineNumber,
							arxivId), e);
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
			logger.error("Failed to generate the summary for a document='{}'",
					arxivId, e);
			throw new GeneratePdfSummaryException(e);
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
		final int number;
		/**
		 * start line of this page in the Latex file
		 */
		final int startLine;
		/**
		 * end line of this page in the Latex file
		 */
		final int endLine;

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
