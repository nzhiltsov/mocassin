package ru.ksu.niimm.cll.mocassin.util;

public class Triple<S, P, O> {
	private final S s;
	private final P p;
	private final O o;

	public Triple(S s, P p, O o) {
		this.s = s;
		this.p = p;
		this.o = o;
	}

	public S getS() {
		return s;
	}

	public P getP() {
		return p;
	}

	public O getO() {
		return o;
	}

}
