package ru.ksu.niimm.ose.ontology.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.ose.ontology.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyRelation;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class OMDocOntologyLoaderImpl implements OMDocOntologyLoader {
	private static final String RDFS_LABEL_LOCALE = "ru";
	private static final String FILE_PATH = "/omdoc.owl";
	private static final String OMDOC_NAMESPACE = "http://omdoc.org/ontology#";
	private OntModel omdocOntology;

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
		String uri = relation.getUri();
		OntProperty ontProperty = getOmdocOntology().getOntProperty(uri);
		ExtendedIterator<? extends OntResource> rangeIterator = ontProperty
				.listRange();
		List<? extends OntResource> rangeList = rangeIterator.toList();
		List<OntologyConcept> rangeConcepts = new ArrayList<OntologyConcept>();
		List<OntClass> rangeClasses = new ArrayList<OntClass>();
		for (OntResource rangeResource : rangeList) {
			if (rangeResource instanceof OntClass) {
				OntClass rangeClass = (OntClass) rangeResource;
				if (hasOmdocNamespace(rangeClass)) {
					List<OntClass> subClasses = rangeClass.listSubClasses()
							.toList();
					boolean hasSubClassAlready = false;
					for (OntClass subClass : subClasses) {
						hasSubClassAlready = rangeClasses.contains(subClass);
						if (hasSubClassAlready)
							break;
					}
					if (!rangeClasses.contains(rangeClass)
							&& !hasSubClassAlready) {
						rangeClasses.add(rangeClass);
						OntologyConcept rangeConcept = new OntologyConcept(
								rangeClass.getURI(), rangeClass.getLabel(RDFS_LABEL_LOCALE));
						rangeConcepts.add(rangeConcept);
					}

				}

			}

		}
		return rangeConcepts;
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
			try {
				omdocOntology = load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return omdocOntology;
	}

	private OntModel load() throws IOException {
		OntModel omdocOntology = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getResourceAsStream(FILE_PATH);
			omdocOntology.read(inputStream, null, "RDF/XML-ABBREV");
			return omdocOntology;
		} catch (Exception e) {
			if (inputStream != null)
				inputStream.close();
			throw new IOException("cannot read ontology");
		}

	}

}
