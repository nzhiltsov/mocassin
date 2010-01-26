package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ru.ksu.niimm.ose.ontology.OMDocOntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.loader.impl.OMDocOntologyLoaderImpl;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OMDocOntologyFacadeImpl implements OMDocOntologyFacade {
	private static final String RDFS_LABEL_LOCALE = "en";

	private static final String OMDOC_NAMESPACE = "http://omdoc.org/ontology#";
	private OntModel omdocOntology;
	private OMDocOntologyLoader ontologyLoader = new OMDocOntologyLoaderImpl();

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
		ExtendedIterator<OntProperty> propertiesIterator = getOmdocOntology()
				.getOntClass(conceptUri).listDeclaredProperties();
		List<OntProperty> propertiesAsList = propertiesIterator.toList();
		List<OntologyRelation> relations = new ArrayList<OntologyRelation>();
		for (OntProperty property : propertiesAsList) {
			String namespace = property.getNameSpace();
			if (namespace.equals(OMDOC_NAMESPACE)
					&& !property.isDatatypeProperty()) {
				String uri = property.getURI();
				String rdfsLabel = property.getLabel(RDFS_LABEL_LOCALE);
				OntologyRelation relation = new OntologyRelation(uri, rdfsLabel);
				relations.add(relation);
			}

		}
		return relations;
	}

	@Override
	public List<OntologyConcept> getOntPropertyRangeList(
			OntologyRelation relation) {
		List<OntologyConcept> rangeConcepts = new ArrayList<OntologyConcept>();
		Stack<OntClass> stack = new Stack<OntClass>();

		List<? extends OntResource> rangeList = getRangeClassesList(relation);
		for (OntResource ontResource : rangeList) {
			if (ontResource instanceof OntClass
					&& ontResource.getNameSpace() != null
					&& ontResource.getNameSpace().equals(OMDOC_NAMESPACE)) {
				OntClass resourceAsOntClass = ontResource.asClass();
				List<OntClass> resourceSubClasses = resourceAsOntClass
						.listSubClasses().toList();
				boolean found = false;
				for (OntClass subClass : resourceSubClasses) {
					if (stack.contains(subClass)) {
						found = true;
					}
				}
				if (!found) {
					stack.push(resourceAsOntClass);
				}

			}
		}

		while (!stack.isEmpty()) {
			OntClass currentClass = stack.pop();
			OntologyConcept rangeConcept = new OntologyConcept(currentClass
					.getURI(), currentClass.getLabel(RDFS_LABEL_LOCALE));
			if (!rangeConcepts.contains(rangeConcept)) {
				rangeConcepts.add(rangeConcept);
			}

			List<OntClass> subClasses = currentClass.listSubClasses().toList();
			for (OntClass subClass : subClasses) {
				stack.push(subClass);
			}
		}

		return rangeConcepts;
	}

	private List<? extends OntResource> getRangeClassesList(
			OntologyRelation relation) {
		String uri = relation.getUri();
		OntProperty ontProperty = getOmdocOntology().getOntProperty(uri);
		ExtendedIterator<? extends OntResource> rangeIterator = ontProperty
				.listRange();
		List<? extends OntResource> rangeList = rangeIterator.toList();
		return rangeList;
	}

	@Override
	public List<OntologyIndividual> getIndividuals(
			OntologyConcept ontologyConcept) {
		// TODO stub to show some individuals of class Symbol only
		List<OntologyIndividual> individuals = new ArrayList<OntologyIndividual>();
		if (ontologyConcept.getUri().equals("http://omdoc.org/ontology#Symbol")) {
			individuals.add(new OntologyIndividual(
					"http://www.openmath.org/cd/relation1#eq", "="));
			individuals.add(new OntologyIndividual(
					"http://www.openmath.org/cd/arith1#plus", "+"));
			individuals.add(new OntologyIndividual(
					"http://www.openmath.org/cd/latexml#closed-interval",
					"[*,*]"));
			individuals.add(new OntologyIndividual(
					"http://www.openmath.org/cd/latexml#divide", "/"));
		}
		return individuals;
	}

	private boolean hasOmdocNamespace(OntClass ontClass) {
		String namespace = ontClass.getNameSpace();
		return namespace != null && namespace.equals(OMDOC_NAMESPACE);
	}

	private OntModel getOmdocOntology() {
		if (omdocOntology == null) {
			omdocOntology = this.ontologyLoader.getOntology();
		}
		return omdocOntology;
	}

}
