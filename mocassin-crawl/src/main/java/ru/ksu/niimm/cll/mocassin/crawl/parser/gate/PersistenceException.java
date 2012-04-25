package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

@SuppressWarnings("serial")
public class PersistenceException extends Exception {

	public PersistenceException(String message) {
		super(message);
	}

	public PersistenceException(Throwable cause) {
		super(cause);
	}

}
