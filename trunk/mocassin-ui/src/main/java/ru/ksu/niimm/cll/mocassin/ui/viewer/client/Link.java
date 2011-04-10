package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Link implements Serializable{

	private static final int DEFAULT_VALUE = 1;

	private int source;

	private int target;

	private double value;

	private int typeCode;
	
	public Link() {}

	private Link(int source, int target) {
		this.source = source;
		this.target = target;
	}

	public Link(int source, int target, int type) {
		this(source, target);
		this.typeCode = type;
		this.value = DEFAULT_VALUE;
	}

	public Link(int source, int target, double value, int type) {
		this(source, target);
		this.typeCode = type;
		this.value = value;
	}

	public int getSource() {
		return source;
	}

	public int getTarget() {
		return target;
	}

	public double getValue() {
		return value;
	}

	public int getTypeCode() {
		return typeCode;
	}

}