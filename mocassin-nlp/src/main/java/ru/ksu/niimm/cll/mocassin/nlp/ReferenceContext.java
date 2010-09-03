package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

public interface ReferenceContext {
	String getTo();

	void setTo(String to);

	String getFrom();

	void setFrom(String from);

	String getFilename();

	void setFilename(String filename);

	String getRefid();

	void setRefid(String refid);

	List<Feature> getFeatures();

	void setFeatures(List<Feature> features);
}
