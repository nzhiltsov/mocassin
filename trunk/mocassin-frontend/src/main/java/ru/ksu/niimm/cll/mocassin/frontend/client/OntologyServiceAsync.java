package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>OntologyService</code>.
 */
public interface OntologyServiceAsync {

	void getConceptList(AsyncCallback<List<OntConcept>> callback);

	void getRelationList(OntConcept concept,
			AsyncCallback<List<OntRelation>> callback);

	void getRelationRangeConceptList(OntRelation relation,
			AsyncCallback<List<OntElement>> callback);
}
