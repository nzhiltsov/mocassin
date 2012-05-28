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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

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
	FileInputStream stream = new FileInputStream(new File(
		"<filepath>"));
	try {
	    model.read(stream, null, "RDF/XML");
	} finally {
	    stream.close();
	}

    }

    @Test
    public void testRefactorURIs() throws IOException {
	String modelSource = OntologyReportGenerator.refactor(model);
	FileWriter fw = new FileWriter("/tmp/OntoMath-pro-1.7.owl");
	fw.write(modelSource);
	fw.flush();
	fw.close();
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
	print("/tmp/classes-ids.csv", ontologyReport.getAllClasses());
	print("/tmp/empty-classes.txt",
		ontologyReport.getClassesWithEmptyComments());
    }

    private void print(String path, Set<String> classesIds) throws IOException {
	FileWriter fw = new FileWriter(path);
	for (String clazz : classesIds) {
	    fw.write(clazz);
	    fw.write("\n");
	}
	fw.flush();
	fw.close();
    }

}
