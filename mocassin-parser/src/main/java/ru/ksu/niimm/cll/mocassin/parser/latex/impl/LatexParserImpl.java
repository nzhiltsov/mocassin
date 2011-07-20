package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.ReferenceEntry;
import net.sourceforge.texlipse.texparser.LatexLexer;
import net.sourceforge.texlipse.texparser.LatexParser;
import net.sourceforge.texlipse.texparser.lexer.LexerException;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.NewtheoremCommand;
import ru.ksu.niimm.cll.mocassin.parser.latex.PdfReferenceEntry;
import ru.ksu.niimm.cll.mocassin.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;

public class LatexParserImpl implements Parser {
	private static final String NUMBERING_WITHIN_SECTION_FLAG = "[section]";
	private static final String LATEX_COMMENT_SYMBOL = "%";
	/**
	 * buffer size while reading the preamble of a document
	 */
	private static final int PREAMBLE_MAX_SIZE = 10 * 1024 * 1024;
	private static final Pattern UNNUMBERED_NEW_THEOREM_PATTERN = Pattern
			.compile("(\\\\newtheorem(\\*){1}\\{.+\\}(\\[.+\\])?\\{.+\\})");
	private static final Pattern NUMBERED_NEW_THEOREM_PATTERN = Pattern
			.compile("(\\\\newtheorem\\{.+\\}(\\[.+\\])?\\{.+\\})");
	private static final Pattern BEGIN_DOCUMENT_PATTERN = Pattern
			.compile("\\\\begin\\{document\\}");

	@Inject
	private Logger logger;

	private LatexParser latexParser = new LatexParser();

	private LatexLexer latexLexer;
	@Inject
	private Latex2PDFMapper latex2pdfMapper;

	private String docId;

	@Override
	public LatexDocumentModel parse(String docId,
			final InputStream inputStream, boolean closeStream) {
		this.docId = docId;
		InputStream parsingInputStream;
		if (!inputStream.markSupported()) {
			parsingInputStream = new BufferedInputStream(inputStream);
		} else {
			parsingInputStream = inputStream;
		}
		parsingInputStream.mark(PREAMBLE_MAX_SIZE);

		List<NewtheoremCommand> newtheorems = new ArrayList<NewtheoremCommand>();

		BufferedReader headerReader = new BufferedReader(new InputStreamReader(
				parsingInputStream));

		boolean isNumberingWithinSection = false;
		String line;
		try {
			while ((line = headerReader.readLine()) != null) {

				int commentBeginningPosition = line
						.indexOf(LATEX_COMMENT_SYMBOL);
				final String strippedLine = commentBeginningPosition >= 0 ? line
						.substring(0, commentBeginningPosition) : line;

				if (strippedLine.length() == 0)
					continue;

				boolean isEndOfPreamble = BEGIN_DOCUMENT_PATTERN.matcher(
						strippedLine).find();
				if (isEndOfPreamble) {
					break;
				}

				Matcher unnumberedNewTheoremMatcher = UNNUMBERED_NEW_THEOREM_PATTERN
						.matcher(strippedLine);
				String unnumberedNewtheoremCommand = null;
				if (unnumberedNewTheoremMatcher.find()) {
					unnumberedNewtheoremCommand = unnumberedNewTheoremMatcher
							.group(1);
				}
				Matcher numberedNewTheoremMatcher = NUMBERED_NEW_THEOREM_PATTERN
						.matcher(strippedLine);
				String numberedNewtheoremCommand = null;
				if (numberedNewTheoremMatcher.find()) {
					numberedNewtheoremCommand = numberedNewTheoremMatcher
							.group(1);
				}
				String newtheoremCommand = null;
				boolean isNumbered = false;
				if (numberedNewtheoremCommand != null) {
					newtheoremCommand = numberedNewtheoremCommand;
					isNumbered = true;
				} else if (unnumberedNewtheoremCommand != null) {
					newtheoremCommand = unnumberedNewtheoremCommand;
				}
				if (newtheoremCommand != null) {

					int firstLeftBrace = newtheoremCommand.indexOf("{") + 1;
					int firstRightBrace = newtheoremCommand.indexOf("}",
							firstLeftBrace);
					String key = newtheoremCommand.substring(firstLeftBrace,
							firstRightBrace);
					int secondLeftBrace = newtheoremCommand.indexOf("{",
							firstRightBrace) + 1;
					String dirtyTitle = newtheoremCommand.substring(
							secondLeftBrace,
							newtheoremCommand.indexOf("}", secondLeftBrace));
					String title = StringUtil.takeoutMarkup(dirtyTitle);
					newtheorems.add(new NewtheoremCommand(key, title,
							isNumbered));
					if (!isNumberingWithinSection
							&& strippedLine
									.contains(NUMBERING_WITHIN_SECTION_FLAG)) {
						isNumberingWithinSection = true;
					}
				}

			}

			parsingInputStream.reset();
			Reader reader = new InputStreamReader(parsingInputStream);
			LatexDocumentModel parsedModel = parseTree(reader);
			parsedModel.setDocId(this.docId);
			parsedModel.setNewtheorems(newtheorems);
			parsedModel.setNumberingWithinSection(isNumberingWithinSection);
			if (closeStream) {
				headerReader.close();
				parsingInputStream.close();
			}
			return parsedModel;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "failed to parse a document model due to:"
					+ e.getMessage());
			return null;
		}
	}

	private LatexDocumentModel parseTree(Reader reader) throws LexerException,
			IOException {
		PushbackReader in = new PushbackReader(reader, 1024);
		setLatexLexer(new LatexLexer(in));

		getLatexParser().parse(getLatexLexer(), false);

		LatexDocumentModel model = fillModel();

		return model;
	}

	private LatexDocumentModel fillModel() {
		List<DocumentReference> references = getLatexParser().getRefs();
		Collections.sort(references, new DocumentReferenceComparator());
		LatexDocumentModel model = new LatexDocumentModel(getLatexParser()
				.getOutlineTree());
		model.setReferences(references);
		List<ReferenceEntry> labels = getLatexParser().getLabels();
		List<PdfReferenceEntry> pdfLabels = new LinkedList<PdfReferenceEntry>();
		for (ReferenceEntry label : labels) {
			int pdfPageNumber = latex2pdfMapper.getPDFPageNumber(
					label.startLine, this.docId);
			PdfReferenceEntry entry = new PdfReferenceEntry(label,
					pdfPageNumber);
			pdfLabels.add(entry);
		}
		model.setLabels(pdfLabels);
		model.setDocumentRoot(getLatexParser().getDocumentEnv());

		model.setCommands(getLatexParser().getCommands());
		return model;
	}

	private LatexParser getLatexParser() {
		return latexParser;
	}

	private LatexLexer getLatexLexer() {
		return latexLexer;
	}

	private void setLatexLexer(LatexLexer latexLexer) {
		this.latexLexer = latexLexer;
	}

	@SuppressWarnings("serial")
	private static class DocumentReferenceComparator implements Serializable,
			Comparator<DocumentReference> {

		@Override
		public int compare(DocumentReference firstRef,
				DocumentReference secondRef) {
			if (firstRef == null || secondRef == null)
				return 0;
			return firstRef.getKey().compareTo(secondRef.getKey());
		}

	}

}
