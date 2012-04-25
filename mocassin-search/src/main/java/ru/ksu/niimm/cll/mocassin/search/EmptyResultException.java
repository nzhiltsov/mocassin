package ru.ksu.niimm.cll.mocassin.search;

@SuppressWarnings("serial")
public class EmptyResultException extends Exception {

	public EmptyResultException(Throwable e) {
		super(e);
	}

	public EmptyResultException(String message) {
		super(message);
	}

}
