package ru.ksu.niimm.ose.ontology.loader.impl;

import java.io.IOException;
import java.io.InputStream;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import ru.ksu.niimm.ose.ontology.loader.RDFStorageLoader;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFStorageLoaderImpl implements RDFStorageLoader {
	private static final String FILE_PATH = "/storage.rdf";
	private OntModel rdfStorage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ksu.niimm.ose.ontology.impl.RDFStorageLoader#getRdfStorage()
	 */
	public OntModel getRdfStorage() {
		if (rdfStorage == null) {
			try {
				rdfStorage = load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return rdfStorage;
	}

	private OntModel load() throws IOException {
		OntModel rdfStorage = ModelFactory
				.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getResourceAsStream(FILE_PATH);
			rdfStorage.read(inputStream, null, "RDF/XML-ABBREV");
			return rdfStorage;
		} catch (Exception e) {
			if (inputStream != null)
				inputStream.close();
			throw new IOException("cannot read rdf storage");
		}

	}

}
