package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

/**
 * Annotation of semantics of a reference.
 * <p>
 * A reference is characterized by so-called 'from' and 'to' elements to define
 * direction in a relationship.
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceContext {
	/**
	 * 
	 * @return id of 'to' element
	 */
	String getTo();

	void setTo(String to);
	/**
	 * 
	 * @return id of 'from' element
	 */
	String getFrom();

	void setFrom(String from);

	String getFilename();

	void setFilename(String filename);

	String getRefid();

	void setRefid(String refid);

	List<Feature> getFeatures();

	void setFeatures(List<Feature> features);
}
