package ru.ksu.niimm.cll.mocassin.arxiv;

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

}
