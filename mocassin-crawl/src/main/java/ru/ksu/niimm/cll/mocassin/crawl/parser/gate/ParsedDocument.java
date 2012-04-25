package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

/**
 * The model represents GATE document parsed with required plugins
 * 
 * @author nzhiltsov
 * 
 */
public interface ParsedDocument {
	String getUri();

	String getPdfUri();

	long getSize();
	
	String getCollectionId();
}
