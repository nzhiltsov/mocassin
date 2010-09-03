package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.Vector;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;

public class PosFeature implements Feature {
	private Vector<String> leftTags;
	private Vector<String> rightTags;

	public PosFeature(Vector<String> leftTags, Vector<String> rightTags) {
		this.leftTags = leftTags;
		this.rightTags = rightTags;
	}

	public Vector<String> getLeftTags() {
		return leftTags;
	}

	public void setLeftTags(Vector<String> leftTags) {
		this.leftTags = leftTags;
	}

	public Vector<String> getRightTags() {
		return rightTags;
	}

	public void setRightTags(Vector<String> rightTags) {
		this.rightTags = rightTags;
	}

}
