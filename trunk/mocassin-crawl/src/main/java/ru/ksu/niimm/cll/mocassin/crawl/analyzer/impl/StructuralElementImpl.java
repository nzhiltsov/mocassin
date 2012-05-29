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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import java.io.Serializable;
import java.util.*;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Token;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;

import com.google.common.base.Predicate;

public class StructuralElementImpl implements StructuralElement {
	/**
	 * added for compatibility; for genuine uniqueness see
	 * {@link #equals(Object)}, which is used URI field value
	 */
	private final int id;
	private final String uri;
	private final long start;
	private final long end;
	private final String name;
	private List<String> labels;
	private final String title;
	private List<Token> contents = new LinkedList<Token>();

	private int startPageNumber;
	private MocassinOntologyClasses predictedClass;
	private int latexStartLine;
	private int latexEndLine;

	public static class Builder {
		private final int id;
		private final String uri;
		private long start;
		private long end;
		private String name;
		private String title;

		public Builder(int id, String uri) {
			this.id = id;
			this.uri = uri;
		}

		public Builder start(long start) {
			this.start = start;
			return this;
		}

		public Builder end(long end) {
			this.end = end;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public StructuralElement build() {
			return new StructuralElementImpl(this);
		}

	}

	public StructuralElementImpl(Builder builder) {
		this.id = builder.id;
		this.uri = builder.uri;
		this.start = builder.start;
		this.end = builder.end;
		this.name = builder.name;
		this.title = builder.title;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getTitle() {
		return title;
	}

	public int getId() {
		return id;
	}

	public String getUri() {
		return uri;
	}

	public long getGateStartOffset() {
		return start;
	}

	public long getGateEndOffset() {
		return end;
	}

	public String getName() {
		return name;
	}

	public MocassinOntologyClasses getPredictedClass() {
		return predictedClass;
	}

	public void setPredictedClass(MocassinOntologyClasses predictedClass) {
		this.predictedClass = predictedClass;
	}

	public List<String> getContents() {
        List<String> result = new ArrayList<String>();
        for (Token token: contents) {
            result.add(token.getValue());
        }
		return result;
	}

    public List<String> getStemContents() {
        List<String> result = new ArrayList<String>();
        for (Token token: contents) {
            result.add(token.getStem());
        }
        return result;
    }

	public void setContents(Token... contents) {
		Collections.addAll(this.contents, contents);
	}

	public int getStartPageNumber() {
		return startPageNumber;
	}

	public void setStartPageNumber(int startPageNumber) {
		this.startPageNumber = startPageNumber;
	}

	public int getLatexStartLine() {
		return latexStartLine;
	}

	public void setLatexStartLine(int latexStartLine) {
		this.latexStartLine = latexStartLine;
	}

	public int getLatexEndLine() {
		return latexEndLine;
	}

	public void setLatexEndLine(int latexEndLine) {
		this.latexEndLine = latexEndLine;
	}

	@Override
	public String toString() {
		return String.format("%s; %s; %s %d", uri, predictedClass, name, latexStartLine);
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
		StructuralElementImpl other = (StructuralElementImpl) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@SuppressWarnings("serial")
	public static class DescPositionComparator implements Serializable,
			Comparator<StructuralElement> {

		@Override
		public int compare(StructuralElement first, StructuralElement second) {
			if (first.getGateStartOffset() < second.getGateStartOffset())
				return 1;
			if (first.getGateStartOffset() > second.getGateStartOffset())
				return -1;
			if (first.getGateEndOffset() - first.getGateStartOffset() > second
					.getGateEndOffset() - second.getGateStartOffset())
				return 1;
			if (first.getGateEndOffset() - first.getGateStartOffset() < second
					.getGateEndOffset() - second.getGateStartOffset())
				return -1;
			return 0;
		}
	}

	@SuppressWarnings("serial")
	public static class IdComparator implements Serializable,
			Comparator<StructuralElement> {

		@Override
		public int compare(StructuralElement first, StructuralElement second) {
			Integer firstId = first.getId();
			Integer secondId = second.getId();
			return firstId.compareTo(secondId);
		}

	}

	public static class TypePredicate implements Predicate<StructuralElement> {
		private final MocassinOntologyClasses type;

		public TypePredicate(MocassinOntologyClasses type) {
			this.type = type;
		}

		@Override
		public boolean apply(StructuralElement elem) {
			return this.type == elem.getPredictedClass();
		}

	}

	public static class TypeFilterPredicate implements
			Predicate<StructuralElement> {
		private final List<MocassinOntologyClasses> filterTypes;

		public TypeFilterPredicate(MocassinOntologyClasses[] filterTypes) {
			this.filterTypes = Arrays.asList(filterTypes);
		}

		@Override
		public boolean apply(StructuralElement element) {
			MocassinOntologyClasses elementType = element.getPredictedClass();
			return this.filterTypes.contains(elementType);
		}
	}

	public static class IdPredicate implements Predicate<StructuralElement> {
		private final int id;

		public IdPredicate(int id) {
			this.id = id;
		}

		@Override
		public boolean apply(StructuralElement input) {
			return id == input.getId();
		}
	}
}
