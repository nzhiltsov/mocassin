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
package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.ReferenceEntry;
import net.sourceforge.texlipse.texparser.LatexLexer;
import net.sourceforge.texlipse.texparser.LatexParser;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;

/**
 * The class implements parsing a latex document relying on the Texlipse tool.
 * 
 * @author Nikita Zhiltsov
 * 
 */
class TexlipseBasedLatexParser implements Parser {
    private static final String RIGHT_BRACE = "}";
    private static final String LEFT_BRACE = "{";
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

    @InjectLogger
    private Logger logger;

    private final LatexParser latexParser = new LatexParser();

    @Inject
    private Latex2PDFMapper latex2pdfMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public LatexDocumentModel parse(String docId,
	    final InputStream inputStream, String encoding, boolean closeStream) {
	InputStream parsingInputStream;
	if (!inputStream.markSupported()) {
	    parsingInputStream = new BufferedInputStream(inputStream);
	} else {
	    parsingInputStream = inputStream;
	}
	parsingInputStream.mark(PREAMBLE_MAX_SIZE);

	List<NewtheoremCommand> newtheorems = new ArrayList<NewtheoremCommand>();

	boolean isNumberingWithinSection = false;
	String line;
	try {
	    BufferedReader headerReader = new BufferedReader(
		    new InputStreamReader(parsingInputStream, encoding));
	    try {
		while ((line = headerReader.readLine()) != null) {

		    int commentBeginningPosition = line
			    .indexOf(LATEX_COMMENT_SYMBOL);
		    final String strippedLine = commentBeginningPosition >= 0 ? new String(
			    line.substring(0, commentBeginningPosition)) : line;

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

			int firstLeftBrace = newtheoremCommand
				.indexOf(LEFT_BRACE) + 1;
			int firstRightBrace = newtheoremCommand.indexOf(
				RIGHT_BRACE, firstLeftBrace);
			String key = newtheoremCommand.substring(
				firstLeftBrace, firstRightBrace).intern();
			int secondLeftBrace = newtheoremCommand.indexOf(
				LEFT_BRACE, firstRightBrace) + 1;
			String dirtyTitle = newtheoremCommand.substring(
				secondLeftBrace,
				newtheoremCommand.indexOf(RIGHT_BRACE,
					secondLeftBrace)).intern();
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
		LatexDocumentModel parsedModel = parseTree(reader, docId);
		parsedModel.setDocId(docId);
		parsedModel.setNewtheorems(newtheorems);
		parsedModel.setNumberingWithinSection(isNumberingWithinSection);
		return parsedModel;
	    } finally {
		if (closeStream) {
		    headerReader.close();
		    parsingInputStream.close();
		}
	    }

	} catch (Exception e) {
	    logger.error("Failed to parse a Latex document model='{}'.", docId,
		    e);
	    throw new RuntimeException("Failed to parse a Latex model.", e);
	}
    }

    private LatexDocumentModel parseTree(Reader reader, String docId)
	    throws LexerException, IOException {
	PushbackReader in = new PushbackReader(reader, 1024);
	getLatexParser().parse(new LatexLexer(in), false);

	LatexDocumentModel model = fillModel(docId);

	return model;
    }

    private LatexDocumentModel fillModel(String docId) {
	List<DocumentReference> references = getLatexParser().getRefs();
	Collections.sort(references, new DocumentReferenceComparator());
	LatexDocumentModel model = new LatexDocumentModel(getLatexParser()
		.getOutlineTree());
	model.setReferences(references);
	List<ReferenceEntry> labels = getLatexParser().getLabels();
	List<PdfReferenceEntry> pdfLabels = new LinkedList<PdfReferenceEntry>();
	for (ReferenceEntry label : labels) {
	    int pdfPageNumber = latex2pdfMapper.getPDFPageNumber(
		    label.startLine, docId);
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
