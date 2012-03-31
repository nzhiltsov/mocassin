package ru.ksu.niimm.cll.mocassin.ontology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
@Ignore
public class OntologyReportGeneratorTest {

	private OntModel model;

	private XWPFDocument wordDocument;

	@Before
	public void init() throws IOException {
		this.model = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_LITE_MEM);
		FileInputStream stream = new FileInputStream(new File(
				"<some path>"));
		try {
			model.read(stream, null, "RDF/XML");
		} finally {
			stream.close();
		}

	}

	@Test
	public void testGenerate() throws IOException {
		wordDocument = OntologyReportGenerator.generate(model);
		FileOutputStream stream = new FileOutputStream(new File(
				"/tmp/OntologyReport.docx"));
		try {
			wordDocument.write(stream);
		} finally {
			stream.close();
		}
	}

	@Test
	public void read() throws FileNotFoundException, IOException {
		XWPFDocument wordDocumentExample = new XWPFDocument(
				new FileInputStream(new File(
						"/home/nzhiltsov/Documents/OntologyReportExample.docx")));
		wordDocumentExample.getParagraphs();
	}
}
