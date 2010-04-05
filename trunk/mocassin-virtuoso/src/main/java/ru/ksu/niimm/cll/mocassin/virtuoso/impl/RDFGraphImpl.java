package ru.ksu.niimm.cll.mocassin.virtuoso.impl;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;

public class RDFGraphImpl implements RDFGraph {
	private final String username;
	private final String password;
	private final String iri;
	private final String url;

	public static class Builder {
		private final String iri;

		private String username;
		private String password;
		private String url;

		public Builder(String iri) {
			this.iri = iri;
		}

		public Builder username(String username) {
			this.username = username;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder url(String url) {
			this.url = url;
			return this;
		}

		public RDFGraph build() {
			return new RDFGraphImpl(this);
		}
	}

	private RDFGraphImpl(Builder builder) {
		this.iri = builder.iri;
		this.username = builder.username;
		this.password = builder.password;
		this.url = builder.url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getIri() {
		return iri;
	}

	public String getUrl() {
		return url;
	}

}
