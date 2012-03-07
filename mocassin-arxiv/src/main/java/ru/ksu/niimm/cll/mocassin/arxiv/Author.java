package ru.ksu.niimm.cll.mocassin.arxiv;

import com.google.common.base.Function;

/**
 * Author of an arXiv article
 * 
 * @author nzhiltsov
 * 
 */
public class Author {
	private String name;
	private String affiliation;

	public Author(String name, String affiliation) {
		this.name = name;
		this.affiliation = affiliation;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
