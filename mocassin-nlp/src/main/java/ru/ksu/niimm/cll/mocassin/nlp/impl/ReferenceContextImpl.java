package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;
import ru.ksu.niimm.cll.mocassin.nlp.ReferenceContext;

public class ReferenceContextImpl implements ReferenceContext {
	private String from;
	private String to;
	private String refid;
	private String filename;
	private List<Feature> features;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	@Override
	public String toString() {
		return String.format("%s|%s|%s|%s", getFilename(), getRefid(),
				getFrom(), getTo());
	}
}
