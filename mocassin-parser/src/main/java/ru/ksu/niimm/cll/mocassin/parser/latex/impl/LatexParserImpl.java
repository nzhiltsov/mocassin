package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.texparser.LatexLexer;
import net.sourceforge.texlipse.texparser.LatexParser;
import net.sourceforge.texlipse.texparser.lexer.LexerException;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.NewtheoremCommand;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;

public class LatexParserImpl implements Parser {
	/**
	 * buffer size while reading the preamble of a document
	 */
	private static final int PREAMBLE_MAX_SIZE = 10 * 1024 * 1024;
	private static final Pattern NEW_THEOREM_PATTERN = Pattern
			.compile("\\\\newtheorem(\\*)?\\{.+\\}(\\[.+\\])?\\{.+\\}");
	private static final Pattern BEGIN_DOCUMENT_PATTERN = Pattern
			.compile("\\\\begin\\{document\\}");

	@Inject
	private Logger logger;

	private LatexParser latexParser = new LatexParser();

	private LatexLexer latexLexer;

	@Override
	public LatexDocumentModel parse(final InputStream inputStream,
			boolean closeStream) {
		InputStream parsingInputStream;
		if (!inputStream.markSupported()) {
			parsingInputStream = new BufferedInputStream(inputStream);
		} else {
			parsingInputStream = inputStream;
		}
		parsingInputStream.mark(PREAMBLE_MAX_SIZE);

		List<NewtheoremCommand> newtheorems = new ArrayList<NewtheoremCommand>();
		Scanner scanner = new Scanner(parsingInputStream, "utf8");

		while (scanner.hasNextLine()) {
			String newtheoremCommand = scanner.findInLine(NEW_THEOREM_PATTERN);
			if (newtheoremCommand != null) {
				int firstLeftBrace = newtheoremCommand.indexOf("{") + 1;
				int firstRightBrace = newtheoremCommand.indexOf("}",
						firstLeftBrace);
				String key = newtheoremCommand.substring(firstLeftBrace,
						firstRightBrace);
				int secondLeftBrace = newtheoremCommand.indexOf("{",
						firstRightBrace) + 1;
				String dirtyTitle = newtheoremCommand.substring(
						secondLeftBrace, newtheoremCommand.indexOf("}",
								secondLeftBrace));
				String title = StringUtil.takeoutMarkup(dirtyTitle);
				newtheorems.add(new NewtheoremCommand(key, title));
			}

			boolean isEndOfPreamble = scanner
					.findInLine(BEGIN_DOCUMENT_PATTERN) != null;
			if (isEndOfPreamble) {
				break;
			}

			scanner.nextLine();

		}

		try {
			parsingInputStream.reset();
			Reader reader = new InputStreamReader(parsingInputStream);
			LatexDocumentModel parsedModel = parseTree(reader);
			parsedModel.setNewtheorems(newtheorems);
			if (closeStream) {
				scanner.close();
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
		model.setLabels(getLatexParser().getLabels());
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
