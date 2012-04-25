package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;

public class TokenImpl implements Token {
	private String value;
	private String pos;

	public TokenImpl(String value, String pos) {
		this.value = value;
		this.pos = pos;
	}

	public String getValue() {
		return value;
	}

	public String getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return value;
	}

}
