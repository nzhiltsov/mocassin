package ru.ksu.niimm.cll.mocassin.ontology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

@Ignore
public class OntologyReportGeneratorTest {

	private OntModel model;

	private OntologyReport ontologyReport;

	@Before
	public void init() throws IOException {
		this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		FileInputStream stream = new FileInputStream(new File("<filepath>"));
		try {
			model.read(stream, null, "RDF/XML");
		} finally {
			stream.close();
		}

	}

	@Test
	public void testGenerate() throws IOException {
		ontologyReport = OntologyReportGenerator.generate(model);
		FileOutputStream stream = new FileOutputStream(new File(
				"/tmp/OntologyReport.docx"));
		try {
			ontologyReport.getWordDocument().write(stream);
		} finally {
			stream.close();
		}
	}

}
