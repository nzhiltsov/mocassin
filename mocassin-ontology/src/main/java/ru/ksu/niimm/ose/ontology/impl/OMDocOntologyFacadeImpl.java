package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

import com.google.inject.Inject;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OMDocOntologyFacadeImpl implements OMDocOntologyFacade {
	private static final String RDFS_LABEL_LOCALE = "en";
	@Inject
	private OMDocOntologyLoader ontologyLoader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ksu.niimm.ose.ontology.OMDocOntologyLoader#getClassNamesList()
	 */
	public List<OntologyConcept> getOntClassList() {
		ExtendedIterator<OntClass> iterator = getOmdocOntology()
				.listNamedClasses();
		List<OntologyConcept> classNamesList = new ArrayList<OntologyConcept>();
		List<OntClass> iteratorAsList = iterator.toList();
		for (OntClass ontClass : iteratorAsList) {
			String rdfsLabel = ontClass.getLabel(RDFS_LABEL_LOCALE);
			String uri = ontClass.getURI();
			if (rdfsLabel != null && !rdfsLabel.equals("")) {
				OntologyConcept concept = new OntologyConcept(uri, rdfsLabel);
				classNamesList.add(concept);
			}

		}
		return classNamesList;
	}

	@Override
	public List<OntologyRelation> getOntPropertyList(
			OntologyConcept ontologyConcept) {
		String conceptUri = ontologyConcept.getUri();
		OntClass ontClass = getOmdocOntology().getOntClass(conceptUri);
		ExtendedIterator<OntProperty> propertiesIterator = ontClass
				.listDeclaredProperties();
		List<OntologyRelation> relations = new ArrayList<OntologyRelation>();
		while (propertiesIterator.hasNext()) {
			OntProperty property = propertiesIterator.next();
			String uri = property.getURI();
			String rdfsLabel = property.getLabel(RDFS_LABEL_LOCALE);
			OntologyRelation relation = new OntologyRelation(uri, rdfsLabel);
			relations.add(relation);

		}
		return relations;
	}

	/*
	 * @Override public List<OntologyConcept> getOntPropertyRangeList(
	 * OntologyRelation relation) { List<OntologyConcept> rangeConcepts = new
	 * ArrayList<OntologyConcept>(); Stack<OntClass> stack = new
	 * Stack<OntClass>();
	 * 
	 * List<? extends OntResource> rangeList = getRangeClassesList(relation);
	 * for (OntResource ontResource : rangeList) { if (ontResource instanceof
	 * OntClass && ontResource.getNameSpace() != null &&
	 * ontResource.getNameSpace().equals(OMDOC_NAMESPACE)) { OntClass
	 * resourceAsOntClass = ontResource.asClass(); List<OntClass>
	 * resourceSubClasses = resourceAsOntClass .listSubClasses().toList();
	 * boolean found = false; for (OntClass subClass : resourceSubClasses) { if
	 * (stack.contains(subClass)) { found = true; } } if (!found) {
	 * stack.push(resourceAsOntClass); }
	 * 
	 * } }
	 * 
	 * while (!stack.isEmpty()) { OntClass currentClass = stack.pop();
	 * OntologyConcept rangeConcept = new OntologyConcept(currentClass
	 * .getURI(), currentClass.getLabel(RDFS_LABEL_LOCALE)); if
	 * (!rangeConcepts.contains(rangeConcept)) {
	 * rangeConcepts.add(rangeConcept); }
	 * 
	 * List<OntClass> subClasses = currentClass.listSubClasses().toList(); for
	 * (OntClass subClass : subClasses) { stack.push(subClass); } }
	 * 
	 * return rangeConcepts; }
	 */

	@Override
	public List<OntologyConcept> getOntPropertyRangeList(
			OntologyRelation relation) {

		Set<OntClass> rangeSet = new HashSet<OntClass>();

		ExtendedIterator<? extends OntResource> rangeIterator = getRangeClassesList(relation);
		while (rangeIterator.hasNext()) {
			OntResource resource = rangeIterator.next();
			if (resource.canAs(OntClass.class)) {
				OntClass ontClass = resource.as(OntClass.class);
				rangeSet.add(ontClass);
				Set<OntClass> subClasses = ontClass.listSubClasses().toSet();
				rangeSet.addAll(subClasses);
			}
		}

		return fillConcepts(rangeSet);

	}

	private List<OntologyConcept> fillConcepts(Set<OntClass> set) {
		List<OntologyConcept> concepts = new ArrayList<OntologyConcept>();
		for (OntClass currentClass : set) {
			String uri = currentClass.getURI();
			if (uri != null) {
				OntologyConcept concept = new OntologyConcept(uri, currentClass
						.getLabel(RDFS_LABEL_LOCALE));
				concepts.add(concept);
			}

		}
		return concepts;
	}

	private ExtendedIterator<? extends OntResource> getRangeClassesList(
			OntologyRelation relation) {
		String uri = relation.getUri();
		OntProperty ontProperty = getOmdocOntology().getOntProperty(uri);
		ExtendedIterator<? extends OntResource> rangeIterator = ontProperty
				.listRange();
		return rangeIterator;
	}

	public OntModel getOmdocOntology() {
		return getOntologyLoader().getOntology();
	}

	public OMDocOntologyLoader getOntologyLoader() {
		return ontologyLoader;
	}

}
