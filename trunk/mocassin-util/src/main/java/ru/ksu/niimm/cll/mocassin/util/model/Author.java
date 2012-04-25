package ru.ksu.niimm.cll.mocassin.util.model;

import com.google.common.base.Function;

/**
 * Author of an arXiv article
 * 
 * @author nzhiltsov
 * 
 */
public class Author {
	private final String uri;
	private final String name;
	private final String affiliation;

	public Author(String uri, String name, String affiliation) {
		this.uri = uri;
		this.name = name;
		this.affiliation = affiliation;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getAffiliation() {
		return affiliation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public static class NameFunction implements Function<Author, String> {

		@Override
		public String apply(Author author) {
			return author.getName();
		}

	}
}
