package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LinkAdapter implements Serializable {
	private int source;

	private int target;

	private double value;

	private int type;

	public LinkAdapter() {
	}

	public LinkAdapter(int source, int target, double value, int type) {
		this.source = source;
		this.target = target;
		this.value = value;
		this.type = type;
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

	public int getType() {
		return type;
	}

}
