package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.Vector;

import ru.ksu.niimm.cll.mocassin.nlp.Feature;

public class WordFeature implements Feature {
	private Vector<String> leftWords;
	private Vector<String> rightWords;

	public WordFeature(Vector<String> leftWords, Vector<String> rightWords) {
		this.leftWords = leftWords;
		this.rightWords = rightWords;
	}

	public Vector<String> getLeftWords() {
		return leftWords;
	}

	public void setLeftWords(Vector<String> leftWords) {
		this.leftWords = leftWords;
	}

	public Vector<String> getRightWords() {
		return rightWords;
	}

	public void setRightWords(Vector<String> rightWords) {
		this.rightWords = rightWords;
	}

}
