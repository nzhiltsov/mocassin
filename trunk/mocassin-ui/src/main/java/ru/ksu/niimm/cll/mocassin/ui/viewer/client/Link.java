package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

public class Link {

	public enum RelationTypes {
		PROVES, REFERS_TO, DEPENDS_ON, HAS_CONSEQUENCE
	}

	private static final int DEFAULT_VALUE = 1;

	private int source;

	private int target;

	private double value;

	private RelationTypes type;

	private Link(int source, int target) {
		this.source = source;
		this.target = target;
	}

	public Link(int source, int target, RelationTypes type) {
		this(source, target);
		this.type = type;
		this.value = DEFAULT_VALUE;
	}

	public Link(int source, int target, double value, RelationTypes type) {
		this(source, target);
		this.type = type;
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

	public RelationTypes getType() {
		return type;
	}

}