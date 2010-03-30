package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import ru.ksu.niimm.ose.ontology.loader.OMDocOntologyLoader;

public class OMDocOntologyLoaderImpl implements OMDocOntologyLoader {
	private static final String FILE_PATH = "/omdoc.owl";
	private OntModel omdocOntology;

	@Override
	public OntModel getOntology() {
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
