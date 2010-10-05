package ru.ksu.niimm.cll.mocassin.nlp;

/**
 * The model represents GATE document parsed with required plugins
 * 
 * @author nzhiltsov
 * 
 */
public interface ParsedDocument {
	String getFilename();

	long getSize();
}
