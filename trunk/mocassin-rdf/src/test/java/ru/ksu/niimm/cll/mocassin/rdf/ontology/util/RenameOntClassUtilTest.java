package ru.ksu.niimm.cll.mocassin.rdf.ontology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RenameOntClassUtilTest {
	private OntModel model;

	private String newModelAsString;

	@Before
	public void init() throws IOException {
		this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		FileInputStream stream = new FileInputStream(new File(
				"/media/HP/docs/OntoMath-pro-0.20.0.owl"));
		try {
			model.read(stream, null, "RDF/XML");
		} finally {
			stream.close();
		}
	}

	@Test
	public void testRefactor() {
		newModelAsString = RenameOntClassesUtil.refactor(model);
	}

	@After
	public void save() throws IOException {
		FileWriter fw = new FileWriter(new File("/tmp/OntoMath-pro-1.0.0.owl"));
		fw.write(newModelAsString);
		fw.flush();
		fw.close();
	}
}
