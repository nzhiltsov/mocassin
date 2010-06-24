package ru.ksu.niimm.cll.mocassin.parser;

/**
 * 
 * Document part
 * 
 * @author nzhiltsov
 * 
 */
public interface Node {

	String getId();

	String getName();

	boolean equals(Object o);

	int hashCode();
}
