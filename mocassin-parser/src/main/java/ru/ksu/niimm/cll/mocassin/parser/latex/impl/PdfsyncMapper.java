package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.parser.Latex2PDFMapper;

import com.csvreader.CsvReader;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * <a href="http://itexmac.sourceforge.net/pdfsync.html">pdfsync<a/> based
 * implementation
 * 
 */
public class PdfsyncMapper implements Latex2PDFMapper {
	@Inject
	private Logger logger;

	private final String pdfsyncDir;

	@Inject
	public PdfsyncMapper(@Named("pdfsync.document.dir") String pdfsyncDir) {
		this.pdfsyncDir = pdfsyncDir;
	}

	@Override
	public int getPDFPageNumber(int latexLineNumber, String documentId) {
		String fileName = documentId.replace('/', '_') + ".pdfsync";
		int foundPageNumber = 0;
		CsvReader reader = null;
		try {
			reader = new CsvReader(pdfsyncDir + fileName, ',');
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
							latexLineNumber, documentId, e.getMessage()));
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return foundPageNumber;
	}

}
