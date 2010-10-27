package ru.ksu.niimm.cll.mocassin.nlp;

/**
 * This interface represents information about a token
 * 
 * @author nzhiltsov
 * 
 */
public interface Token {
	/**
	 * returns string value of a token (or its stem - see configuration)
	 * 
	 * @return
	 */
	String getValue();

	/**
	 * returns POS tag according to GATE tag set: {@link http
	 * ://gate.ac.uk/sale/tao/splitap7.html}
	 * 
	 * @return
	 */
	String getPos();
}
