package ru.ksu.niimm.cll.mocassin.nlp.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;

import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

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
	private List<String> contents = new LinkedList<String>();

	private int startPageNumber;
	private MocassinOntologyClasses predictedClass;

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

	public long getStart() {
		return start;
	}

	public long getEnd() {
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
		return contents;
	}

	public void setContents(String... contents) {
		Collections.addAll(this.contents, contents);
	}

	public int getStartPageNumber() {
		return startPageNumber;
	}

	public void setStartPageNumber(int startPageNumber) {
		this.startPageNumber = startPageNumber;
	}

	@Override
	public String toString() {
		return String.format("%s; %s", uri, predictedClass);
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
	public static class PositionComparator implements Serializable,
			Comparator<StructuralElement> {

		@Override
		public int compare(StructuralElement first, StructuralElement second) {
			if (first.getStart() < second.getStart())
				return -1;
			if (first.getStart() > second.getStart())
				return 1;
			if (first.getEnd() < second.getEnd())
				return -1;
			if (first.getEnd() > second.getEnd())
				return 1;
			return 0;
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
}