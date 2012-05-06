package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
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
		.createInjector(new OntologyTestModule(), new AnalyzerModule(),
			new GateModule(), new LatexParserModule(),
			new NlpModule(), new PdfParserModule());
	return injector;
    }
}
