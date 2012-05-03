package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.parse.Parse;

public class MocassinRdfIndexer implements IndexingFilter {
    
    private Configuration conf;

    public Configuration getConf() {
        return conf;
    }

    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public NutchDocument filter(NutchDocument doc, Parse parse, Text url,
	    CrawlDatum datum, Inlinks inlinks) throws IndexingException {
	// TODO Auto-generated method stub
	return null;
    }

}
