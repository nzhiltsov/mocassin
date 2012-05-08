/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.util.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Article metadata
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class ArticleMetadata {
	/**
	 * Article id, e.g. 'math/0205003' or 'ivm537'
	 */
	private String collectionId;
	/**
	 * Full id, e.g. 'http://arxiv.org/abs/math/0205003v1' where the 'v1' suffix
	 * is a pointer of the version, or 'http://mathnet.ru/ivm537'
	 */
	private String id;
	/**
	 * A list of additional links (PDF source, Latex source etc.)
	 * 
	 */
	private List<Link> links = new ArrayList<Link>();
	/**
	 * Title
	 */
	private String title;
	/**
	 * Authors
	 */
	private final Set<Author> authors = new HashSet<Author>();
	/**
	 * Additional field, which is used on UI
	 */
	private String currentSegmentUri;
	/**
	 * Additional field, which is used on UI
	 */
	private String currentSegmentTitle;
	/**
	 * Additional field, which is used on UI
	 */
	private int currentPageNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors.addAll(authors);
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getCurrentSegmentUri() {
		return currentSegmentUri;
	}

	public void setCurrentSegmentUri(String currentSegmentUri) {
		this.currentSegmentUri = currentSegmentUri;
	}

	public int getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public String getCurrentSegmentTitle() {
		return currentSegmentTitle;
	}

	public void setCurrentSegmentTitle(String currentSegmentTitle) {
		this.currentSegmentTitle = currentSegmentTitle;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

}
