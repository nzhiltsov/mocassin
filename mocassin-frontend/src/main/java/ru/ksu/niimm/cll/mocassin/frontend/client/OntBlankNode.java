package ru.ksu.niimm.cll.mocassin.frontend.client;

@SuppressWarnings("serial")
public class OntBlankNode extends OntElement {

	public OntBlankNode() {
		super();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof OntBlankNode ? true : false;
	}

}
