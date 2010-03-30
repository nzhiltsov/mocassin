package ru.ksu.niimm.ose.ui.client;

@SuppressWarnings("serial")
public class OntIndividual extends OntElement implements
		Comparable<OntIndividual> {

	public OntIndividual() {
		super();
	}

	public OntIndividual(String uri, String label) {
		super(uri, label);
	}

	@Override
	public int compareTo(OntIndividual o) {
		if (o != null && getLabel() != null && o.getLabel() != null) {
			getLabel().compareTo(o.getLabel());
		}
		return 0;
	}

}
