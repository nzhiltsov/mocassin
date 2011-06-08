package ru.ksu.niimm.cll.mocassin.nlp.util;

import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.TokenImpl;

import com.thoughtworks.xstream.XStream;

public class ReferenceXStream extends XStream {

	private static final String DOCUMENT_TAG_NAME = "document";
	private static final String TOKEN_TAG_NAME = "token";
	private static final String REFERENCE_TAG_NAME = "reference";

	public ReferenceXStream() {
		super();
		alias(REFERENCE_TAG_NAME, ReferenceImpl.class);
		alias(TOKEN_TAG_NAME, TokenImpl.class);
		alias(DOCUMENT_TAG_NAME, ParsedDocumentImpl.class);
	}

}
