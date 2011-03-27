package ru.ksu.niimm.ose.ontology.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.loader.OntologyLoader;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyFacadeImpl implements OntologyFacade {
	@Inject
	private OntologyLoader ontologyLoader;

	private String ontologyUri;
	
	private String ontologyLabelLocale;

	@Inject
	public OntologyFacadeImpl(@Named("ontology.uri") String ontologyUri,
			@Named("ontology.label.locale") String locale) {
		this.ontologyUri = ontologyUri;
		this.ontologyLabelLocale = locale;
	}

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
			String rdfsLabel = ontClass.getLabel(getLocale());
			String uri = ontClass.getURI();
			if (rdfsLabel != null && !rdfsLabel.equals("")) {
				OntologyConcept concept = new OntologyConcept(uri, rdfsLabel);
				classNamesList.add(concept);
			}

		}
		return classNamesList;
	}

	private String getLocale() {
		return this.ontologyLabelLocale;
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
			if (!isOmdocProperty(uri)) {
				continue;
			}
			String rdfsLabel = property.getLabel(getLocale());
			OntologyRelation relation = new OntologyRelation(uri, rdfsLabel);
			relations.add(relation);

		}
		return relations;
	}

	private boolean isOmdocProperty(String uri) {
		return uri.startsWith(this.ontologyUri);
	}

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
						.getLabel(getLocale()));
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

	public OntologyLoader getOntologyLoader() {
		return ontologyLoader;
	}

}
