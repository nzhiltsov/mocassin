package ru.ksu.niimm.cll.mocassin.ui.client;

@SuppressWarnings("serial")
public class OntRelation extends OntElement implements Comparable<OntRelation> {
	@Override
	public int compareTo(OntRelation o) {
		if (o != null && getLabel() != null && o.getLabel() != null) {
			getLabel().compareTo(o.getLabel());
		}
		return 0;
	}
}
