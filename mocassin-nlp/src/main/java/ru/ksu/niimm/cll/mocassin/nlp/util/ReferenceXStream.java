package ru.ksu.niimm.cll.mocassin.nlp.util;

import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;

import com.thoughtworks.xstream.XStream;

public class ReferenceXStream extends XStream {

	private static final String REFERENCE_TAG_NAME = "reference";

	public ReferenceXStream() {
		super();
		alias(REFERENCE_TAG_NAME, ReferenceImpl.class);
	}

}
