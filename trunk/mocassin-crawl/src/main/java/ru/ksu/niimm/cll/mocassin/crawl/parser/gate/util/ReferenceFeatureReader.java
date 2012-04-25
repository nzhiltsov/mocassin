package ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

public class ReferenceFeatureReader {
	private ReferenceFeatureReader() {
	}

	public static List<Reference> read(Reader reader) throws IOException,
			ClassNotFoundException {
		throw new UnsupportedOperationException(
				"ReferenceFeatureReader must read from a store");
		/*XStream xstream = new ReferenceXStream();
		List<Reference> refs = new ArrayList<Reference>();
		ObjectInputStream in = xstream.createObjectInputStream(reader);
		try {
			while (true) {
				Reference reference = (Reference) in.readObject();
				if (reference == null)
					continue;
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
		}*/
	}
}
