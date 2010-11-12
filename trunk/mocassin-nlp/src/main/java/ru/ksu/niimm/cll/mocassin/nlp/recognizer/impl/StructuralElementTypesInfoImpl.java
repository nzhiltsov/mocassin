package ru.ksu.niimm.cll.mocassin.nlp.recognizer.impl;

import java.util.SortedMap;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypesInfo;

public class StructuralElementTypesInfoImpl implements
		StructuralElementTypesInfo {
	private Reference reference;
	private SortedMap<String, Float> fromElementVector;
	private SortedMap<String, Float> toElementVector;

	public StructuralElementTypesInfoImpl(Reference reference,
			SortedMap<String, Float> fromElementVector,
			SortedMap<String, Float> toElementVector) {
		this.reference = reference;
		this.fromElementVector = fromElementVector;
		this.toElementVector = toElementVector;
	}

	public Reference getReference() {
		return reference;
	}

	public SortedMap<String, Float> getFromElementVector() {
		return fromElementVector;
	}

	public SortedMap<String, Float> getToElementVector() {
		return toElementVector;
	}

}
