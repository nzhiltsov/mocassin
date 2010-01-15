package ru.ksu.niimm.ose.ui.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ontology")
public interface OntologyService extends RemoteService {
	String greetServer(String name);
	List<OntConcept> getConceptList();
	public List<OntRelation> getRelationList(OntConcept concept);
	public List<OntElement> getRelationRangeConceptList(OntRelation relation);
	public List<ResultDescription> query(OntQueryStatement statement);
}
