package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.io.Serializable;
import java.util.Comparator;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

@SuppressWarnings("serial")
public class StructuralElementByLocationComparator implements
		Serializable, Comparator<StructuralElement> {
	@Override
	public int compare(StructuralElement first, StructuralElement second) {
		return (int) (first.getStart() - second.getStart());
	}
}
