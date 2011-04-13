package ru.ksu.niimm.cll.mocassin.parser.latex.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.texlipse.model.DocumentReference;
import net.sourceforge.texlipse.model.TexCommandEntry;
import net.sourceforge.texlipse.texparser.LatexLexer;
import net.sourceforge.texlipse.texparser.LatexParser;
import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.codehaus.swizzle.stream.ReplaceStringInputStream;

import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TexCommandEntryAdapter;

import com.google.inject.Inject;

public class LatexParserImpl implements Parser {
	@Inject
	private Logger logger;

	private LatexParser latexParser = new LatexParser();

	private LatexLexer latexLexer;

	@Override
	public LatexDocumentModel parse(final InputStream inputStream) {

		final PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in = null;
		try {
			in = new PipedInputStream(out);
		} catch (IOException error) {
			logger.log(Level.SEVERE,
					"failed to create a piped input stream due to"
							+ error.getMessage());
			return null;
		}

		new Thread(new Runnable() {
			public void run() {
				try {
					InputStream replaceInputStream = new ReplaceStringInputStream(
							inputStream, "\\newtheorem{", "\\newcommand{\\");
					try {
						int b;
						while ((b = replaceInputStream.read()) != -1) {
							out.write(b);
						}
					} finally {
						out.flush();
						out.close();
					}

				} catch (IOException e) {
					logger.log(Level.SEVERE,
							"failed to replace strings in a stream due to:"
									+ e.getMessage());
				}
			}
		}).start();

		try {
			Reader reader = new InputStreamReader(in);
			LatexDocumentModel parsedModel = parseTree(reader);
			if (in != null)
				in.close();
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

		List<TexCommandEntry> entries = getLatexParser().getCommands();
		List<TexCommandEntryAdapter> commands = new ArrayList<TexCommandEntryAdapter>();
		for (TexCommandEntry entry : entries) {
			commands.add(new TexCommandEntryAdapter(entry));
		}
		model.setCommands(commands);
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
