package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.crawl.ArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.mathnet.MathnetModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinParser implements Parser {

	private Configuration conf;

	private ArXMLivAdapter arXMLivAdapter;

	private Logger logger;

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
		Injector injector = Guice
				.createInjector(new MathnetModule(), new OntologyModule(),
						new AnalyzerModule(), new GateModule(),
						new LatexParserModule(), new NlpModule(),
						new PdfParserModule());
		arXMLivAdapter = injector.getInstance(ArXMLivAdapter.class);
		logger = injector.getInstance(Logger.class);
	}

	@Override
	public ParseResult getParse(Content content) {
		String baseUrl = content.getBaseUrl();
		String mathnetKey = StringUtil.extractMathnetKeyFromURI(baseUrl);
		try {
			arXMLivAdapter.handle(mathnetKey);
		} catch (Throwable e) {
			logger.error("Failed to parse the content of a document={}",
					baseUrl, e);
		}
		return null;
	}

}
