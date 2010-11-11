package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.util.Comparator;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;

public class StructuralElementByLocationComparator implements
		Comparator<StructuralElement> {
	@Override
	public int compare(StructuralElement first, StructuralElement second) {
		return (int) (first.getStart() - second.getStart());
	}
}
