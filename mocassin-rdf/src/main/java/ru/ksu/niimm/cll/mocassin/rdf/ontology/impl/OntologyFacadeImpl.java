/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.rdf.ontology.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.*;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.*;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.OntologyProvider;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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
    public List<MocassinOntologyRelations> getRelations(MocassinOntologyClasses first, MocassinOntologyClasses second) {
        OntClass firstClass = getOntology().getOntClass(MocassinOntologyClasses.getUri(first));
        OntClass secondClass = getOntology().getOntClass(MocassinOntologyClasses.getUri(second));

        ArrayList<MocassinOntologyRelations> result = new ArrayList<MocassinOntologyRelations>();

        ExtendedIterator<ObjectProperty> propertyIterator = getOntology().listObjectProperties();
        while(propertyIterator.hasNext()) {
            ObjectProperty property = propertyIterator.next();
            if (property.getNameSpace() == null) continue;
            if (isInDomain(firstClass, property) && isInRange(secondClass, property)) {
                try {
                    result.add(MocassinOntologyRelations.fromUri(property.getURI()));
                } catch (RuntimeException e) {
                    //not relation we are interested in
                }
            }
        }

        return result;
    }

    private boolean isInRange(OntClass ontClass, OntProperty ontProperty) {
        ExtendedIterator<? extends OntResource> rangeIterator = ontProperty.listRange();

        List<OntClass> rangeList = new ArrayList<OntClass>();
        while (rangeIterator.hasNext()) {
            OntResource resource = rangeIterator.next();
            if (resource.canAs(OntClass.class)) {
                OntClass ontClass2 = resource.as(OntClass.class);
                rangeList.add(ontClass2);
                List<OntClass> subClasses = ontClass2.listSubClasses().toList();
                rangeList.addAll(subClasses);
            }
        }

        return rangeList.contains(ontClass);
    }

    private boolean isInDomain(OntClass ontClass, OntProperty ontProperty) {
        ExtendedIterator<? extends OntResource> domainIterator = ontProperty.listDomain();

        List<OntClass> domainList = new ArrayList<OntClass>();
        while (domainIterator.hasNext()) {
            OntResource resource = domainIterator.next();
            if (resource.canAs(OntClass.class)) {
                OntClass ontClass2 = resource.as(OntClass.class);
                domainList.add(ontClass2);
                List<OntClass> subClasses = ontClass2.listSubClasses().toList();
                domainList.addAll(subClasses);
            }
        }

        return domainList.contains(ontClass);
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
		List<OntologyRelation> relations = new ArrayList<OntologyRelation>();

		ExtendedIterator<ObjectProperty> allObjectProperties = getOntology()
				.listObjectProperties();
		fillRelationsForClass(ontClass, allObjectProperties, relations);

		ExtendedIterator<DatatypeProperty> allDatatypeProperties = getOntology()
				.listDatatypeProperties();
		fillRelationsForClass(ontClass, allDatatypeProperties, relations);
		return relations;
	}

	private void fillRelationsForClass(OntClass ontClass,
			ExtendedIterator<? extends OntProperty> properties,
			List<OntologyRelation> relations) {
		while (properties.hasNext()) {
			OntProperty property = properties.next();
			if (property.getNameSpace() == null) continue;
			if (ontClass.hasDeclaredProperty(property, false)) {
				String uri = property.getURI();
				String rdfsLabel = property.getLabel(getLocale());
				OntologyRelation relation = new OntologyRelation(uri, rdfsLabel);
				relations.add(relation);
			}
		}
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

    @Override
    public List<MocassinOntologyClasses> getDomainClasses(MocassinOntologyRelations relation) {
        List<MocassinOntologyClasses> result = new ArrayList<MocassinOntologyClasses>();
        OntProperty ontProperty = getOntology().getOntProperty(relation.getUri());

        ExtendedIterator<? extends OntResource> domainIterator = ontProperty.listDomain();

        while (domainIterator.hasNext()) {
            OntResource resource = domainIterator.next();
            if (resource.canAs(OntClass.class)) {
                OntClass ontClass = resource.as(OntClass.class);
                if (MocassinOntologyClasses.fromUri(ontClass.getURI()) != null) {
                    result.add(MocassinOntologyClasses.fromUri(ontClass.getURI()));
                }
                List<OntClass> subClasses = ontClass.listSubClasses().toList();
                for (OntClass subClass: subClasses) {
                    if (MocassinOntologyClasses.fromUri(subClass.getURI()) != null) {
                        result.add(MocassinOntologyClasses.fromUri(subClass.getURI()));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<MocassinOntologyClasses> getRangeClasses(MocassinOntologyRelations relation) {
        List<MocassinOntologyClasses> result = new ArrayList<MocassinOntologyClasses>();
        OntProperty ontProperty = getOntology().getOntProperty(relation.getUri());

        ExtendedIterator<? extends OntResource> domainIterator = ontProperty.listRange();

        while (domainIterator.hasNext()) {
            OntResource resource = domainIterator.next();
            if (resource.canAs(OntClass.class)) {
                OntClass ontClass = resource.as(OntClass.class);
                if (MocassinOntologyClasses.fromUri(ontClass.getURI()) != null) {
                    result.add(MocassinOntologyClasses.fromUri(ontClass.getURI()));
                }
                List<OntClass> subClasses = ontClass.listSubClasses().toList();
                for (OntClass subClass: subClasses) {
                    if (MocassinOntologyClasses.fromUri(subClass.getURI()) != null) {
                        result.add(MocassinOntologyClasses.fromUri(subClass.getURI()));
                    }
                }
            }
        }

        return result;
    }
}
