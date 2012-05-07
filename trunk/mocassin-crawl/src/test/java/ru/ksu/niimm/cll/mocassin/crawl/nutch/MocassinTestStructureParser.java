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
package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.DocumentAnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinTestStructureParser extends MocassinStructureParser {
    @Override
    protected Injector createInjector() {
	Injector injector = Guice
		.createInjector(new OntologyTestModule(), new DocumentAnalyzerModule(),
			new GateModule(), new LatexParserModule(),
			new NlpModule(), new PdfParserModule());
	return injector;
    }
}
