package ru.ksu.niimm.cll.mocassin.ontology.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyRelation;
import ru.ksu.niimm.cll.mocassin.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OntologyFacadeImpl implements OntologyFacade {
	
	@InjectLogger
	private Logger logger;

	private final OntologyProvider<OntModel> ontologyProvider;

	private final String ontologyUri;

	private final String ontologyLabelLocale;

	@Inject
	public OntologyFacadeImpl(OntologyProvider<OntModel> ontologyProvider,
			@Named("ontology.uri") String ontologyUri,
			@Named("ontology.label.locale") String locale) {
		this.ontologyProvider = ontologyProvider;
		this.ontologyUri = ontologyUri;
		this.ontologyLabelLocale = locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ksu.niimm.ose.ontology.OMDocOntologyLoader#getClassNamesList()
	 */
	public List<OntologyConcept> getOntClassList() {
		ExtendedIterator<OntClass> iterator = getOntology().listNamedClasses();
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

	@Override
	public MocassinOntologyClasses getMoreSpecific(
			MocassinOntologyClasses first, MocassinOntologyClasses second) {
		if (first.equals(second))
			return first;
		OntClass firstOntClass = getOntology().getOntClass(
				MocassinOntologyClasses.getUri(first));
		OntClass secondOntClass = getOntology().getOntClass(
				MocassinOntologyClasses.getUri(second));
		return firstOntClass.listSubClasses().toSet().size() < secondOntClass
				.listSubClasses().toSet().size() ? first : second;
	}

	@Override
	public MocassinOntologyClasses getMostSpecific(
			List<MocassinOntologyClasses> hierarchy) {
		if (hierarchy.isEmpty())
			throw new IllegalArgumentException("hierarchy cannon be empty");
		if (hierarchy.size() == 1)
			return hierarchy.get(0);
		int minChildCount = Integer.MAX_VALUE;
		MocassinOntologyClasses minClass = null;
		for (MocassinOntologyClasses concept : hierarchy) {
			OntClass ontClass = getOntology().getOntClass(
					MocassinOntologyClasses.getUri(concept));
			int childCount = ontClass.listSubClasses().toSet().size();
			if (childCount < minChildCount) {
				minChildCount = childCount;
				minClass = concept;
			}
		}
		return minClass;
	}

	private String getLocale() {
		return this.ontologyLabelLocale;
	}

	@Override
	public List<OntologyRelation> getOntPropertyList(
			OntologyConcept ontologyConcept) {
		String conceptUri = ontologyConcept.getUri();
		OntClass ontClass = getOntology().getOntClass(conceptUri);
		ExtendedIterator<OntProperty> propertiesIterator = ontClass
				.listDeclaredProperties();
		List<OntologyRelation> relations = new ArrayList<OntologyRelation>();
		while (propertiesIterator.hasNext()) {
			OntProperty property = propertiesIterator.next();
			String uri = property.getURI();
			if (!isOntologyProperty(uri)) {
				continue;
			}
			String rdfsLabel = property.getLabel(getLocale());
			OntologyRelation relation = new OntologyRelation(uri, rdfsLabel);
			relations.add(relation);

		}
		return relations;
	}

	private boolean isOntologyProperty(String uri) {
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
				OntologyConcept concept = new OntologyConcept(uri,
						currentClass.getLabel(getLocale()));
				concepts.add(concept);
			}

		}
		return concepts;
	}

	private ExtendedIterator<? extends OntResource> getRangeClassesList(
			OntologyRelation relation) {
		String uri = relation.getUri();
		OntProperty ontProperty = getOntology().getOntProperty(uri);
		ExtendedIterator<? extends OntResource> rangeIterator = ontProperty
				.listRange();
		return rangeIterator;
	}

	public OntModel getOntology() {
		try {
			return this.ontologyProvider.get();
		} catch (IOException e) {
			logger.error("Couldn't get the ontology instance.", e);
			throw new RuntimeException(e);
		}
	}
}
