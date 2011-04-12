package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.texparser.LatexLexer;
import net.sourceforge.texlipse.texparser.LatexParser;
import net.sourceforge.texlipse.texparser.lexer.LexerException;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;

public class TreeParserImpl implements TreeParser {
	private LatexParser latexParser = new LatexParser();
	private LatexLexer latexLexer;

	@Override
	public LatexDocumentModel parseTree(Reader reader) throws LexerException,
			IOException {
		prepareLexer(reader);

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

	private void prepareLexer(Reader reader) {
		PushbackReader in = new PushbackReader(reader, 1024);
		setLatexLexer(new LatexLexer(in));
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

	private class DocumentReferenceComparator implements
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
