package ru.ksu.niimm.cll.mocassin.nlp.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.Token;

import com.thoughtworks.xstream.XStream;

public class ReferenceFeatureReader {
	private ReferenceFeatureReader() {
	}

	public static List<Reference> read(Reader reader) throws IOException,
			ClassNotFoundException {
		XStream xstream = new ReferenceXStream();
		List<Reference> refs = new ArrayList<Reference>();
		ObjectInputStream in = xstream.createObjectInputStream(reader);
		try {
			while (true) {
				Reference reference = (Reference) in.readObject();
				if (reference == null) continue;
				if (reference.getTo().getTitleTokens() == null) {
					reference.getTo().setTitleTokens(new ArrayList<Token>());
				}
				if (reference.getFrom().getTitleTokens() == null) {
					reference.getFrom().setTitleTokens(new ArrayList<Token>());
				}
				refs.add(reference);
			}
		} catch (EOFException e) {
			return refs;
		}
	}
}
