package ru.ksu.niimm.cll.mocassin.crawl.mathnet;

import org.apache.nutch.crawl.Crawl;
import org.junit.Test;

public class MocassinParserTest {

	@Test
	public void testParse() throws Exception {
		String[] args = {"/opt/mocassin/urls"};
		Crawl.main(args);
	}
}
