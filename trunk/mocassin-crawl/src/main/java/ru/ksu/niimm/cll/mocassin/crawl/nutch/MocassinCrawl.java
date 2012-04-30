package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import org.apache.nutch.crawl.Crawl;

public class MocassinCrawl {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String[] arguments = { "/opt/mocassin/urls", "-depth", "1",
				"-dir", "/opt/mocassin/crawl" };
		Crawl.main(arguments);
	}

}
