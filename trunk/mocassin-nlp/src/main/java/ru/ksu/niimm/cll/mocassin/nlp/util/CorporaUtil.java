package ru.ksu.niimm.cll.mocassin.nlp.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import gate.Corpus;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.util.ExtensionFileFilter;

public class CorporaUtil {
	private CorporaUtil() {
	}

	public static void main(String[] args)
			throws ResourceInstantiationException, IOException {
		Corpus corpus = Factory.newCorpus("arxmliv-refcontext-corpus");
		File dir = new File("/home/linglab/arxmliv/ref-contexts");
		ExtensionFileFilter filter = new ExtensionFileFilter("XML files", "xml");
		URL url = dir.toURI().toURL();
		corpus.populate(url, filter, null, true);
	}
}
