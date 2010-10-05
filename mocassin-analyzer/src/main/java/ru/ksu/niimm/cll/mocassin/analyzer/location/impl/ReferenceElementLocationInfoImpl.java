package ru.ksu.niimm.cll.mocassin.analyzer.location.impl;

import ru.ksu.niimm.cll.mocassin.analyzer.location.ReferenceElementLocationInfo;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;

public class ReferenceElementLocationInfoImpl implements ReferenceElementLocationInfo {
	private Reference reference;

	private float startDistance;

	private float endDistance;

	public ReferenceElementLocationInfoImpl(Reference reference, float startDistance,
			float endDistance) {
		this.reference = reference;
		this.startDistance = startDistance;
		this.endDistance = endDistance;
	}

	public Reference getReference() {
		return reference;
	}

	public float getStartDistance() {
		return startDistance;
	}

	public float getEndDistance() {
		return endDistance;
	}

}
