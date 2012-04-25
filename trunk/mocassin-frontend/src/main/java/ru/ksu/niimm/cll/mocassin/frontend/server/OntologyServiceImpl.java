package ru.ksu.niimm.cll.mocassin.frontend.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.client.OntConcept;
import ru.ksu.niimm.cll.mocassin.frontend.client.OntElement;
import ru.ksu.niimm.cll.mocassin.frontend.client.OntRelation;
import ru.ksu.niimm.cll.mocassin.frontend.client.OntologyService;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyRelation;

import com.google.inject.Inject;

/**
 * The server side implementation of the RPC service.
 */
public class OntologyServiceImpl implements OntologyService {

	private final OntologyFacade ontologyFacade;

	@Inject
	public OntologyServiceImpl(OntologyFacade omdocOntologyFacade) {
		this.ontologyFacade = omdocOntologyFacade;
	}

	@Override
	public List<OntConcept> getConceptList() {
		List<OntologyConcept> ontClassList = getOmdocOntologyFacade()
				.getOntClassList();
		List<OntConcept> targetConceptList = new ArrayList<OntConcept>();
		for (OntologyConcept concept : ontClassList) {
			OntConcept targetOntConcept = new OntConcept();
			targetOntConcept.setUri(concept.getUri());
			targetOntConcept.setLabel(concept.getLabel());
			targetConceptList.add(targetOntConcept);
		}
		Collections.sort(targetConceptList,
				new OntElement.OntElementComparator<OntConcept>());
		return targetConceptList;
	}

	public List<OntRelation> getRelationList(OntConcept concept) {
		OntologyConcept ontologyConcept = new OntologyConcept(concept.getUri(),
				concept.getLabel());
		List<OntologyRelation> ontologyPropertyList = getOmdocOntologyFacade()
				.getOntPropertyList(ontologyConcept);
		List<OntRelation> targetRelationList = new ArrayList<OntRelation>();
		for (OntologyRelation relation : ontologyPropertyList) {
			OntRelation targetRelation = new OntRelation();
			targetRelation.setUri(relation.getUri());
			targetRelation.setLabel(relation.getLabel());
			targetRelationList.add(targetRelation);
		}
		Collections.sort(targetRelationList,
				new OntElement.OntElementComparator<OntRelation>());
		return targetRelationList;
	}

	public List<OntElement> getRelationRangeConceptList(OntRelation relation) {
		OntologyRelation ontologyRelation = new OntologyRelation(relation
				.getUri(), relation.getLabel());
		List<OntologyConcept> ontologyRangeList = getOmdocOntologyFacade()
				.getOntPropertyRangeList(ontologyRelation);
		List<OntElement> targetConceptList = new ArrayList<OntElement>();
		for (OntologyConcept rangeConcept : ontologyRangeList) {
			OntConcept targetConcept = new OntConcept();
			targetConcept.setUri(rangeConcept.getUri());
			targetConcept.setLabel(rangeConcept.getLabel());
			targetConceptList.add(targetConcept);

		}

		Collections.sort(targetConceptList,
				new OntElement.OntElementComparator<OntElement>());
		return targetConceptList;
	}

	public OntologyFacade getOmdocOntologyFacade() {
		return ontologyFacade;
	}

}
