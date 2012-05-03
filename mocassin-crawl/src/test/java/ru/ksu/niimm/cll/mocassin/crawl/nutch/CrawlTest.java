package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import org.apache.nutch.crawl.Crawl;
import org.junit.Test;

public class CrawlTest {

    @Test
    public void testParse() throws Exception {
	String[] args = { "/opt/mocassin/test-urls", "-solr",
		"http://127.0.0.1:8983/solr/", "-depth", "1", "-dir",
		"/opt/mocassin/test-crawl" };
	Crawl.main(args);
    }
}
