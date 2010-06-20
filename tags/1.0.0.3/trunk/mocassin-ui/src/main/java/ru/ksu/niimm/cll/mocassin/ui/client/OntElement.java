package ru.ksu.niimm.cll.mocassin.ui.client;

import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("serial")
public class OntElement implements Serializable {
	protected String uri;
	protected String label;
	/**
	 * @see ru.ksu.niimm.ose.ontology#OntologyElement correspond with 'id' field
	 *      in that class
	 */
	protected int id;

	public OntElement() {
	}

	public OntElement(String uri, String label) {
		this.uri = uri;
		this.label = label;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "OntElement [label=" + label + ", uri=" + uri + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static class OntElementComparator<T extends OntElement> implements
			Comparator<T> {

		@Override
		public int compare(T firstElement, T secondElement) {
			if (firstElement == null || secondElement == null
					|| firstElement.getLabel() == null
					|| secondElement.getLabel() == null) {
				return 0;
			}
			return firstElement.getLabel().compareTo(secondElement.getLabel());
		}

	}

}
