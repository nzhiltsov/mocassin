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

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;

import ru.ksu.niimm.cll.mocassin.crawl.DomainAdapter;
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

	protected static final Outlink[] NO_OUTLINKS = new Outlink[0];

	protected Configuration conf;

	private DomainAdapter domainAdapter;

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
		Injector injector = createInjector();
		domainAdapter = injector.getInstance(DomainAdapter.class);
	}

	protected Injector createInjector() {
		Injector injector = Guice.createInjector(new MathnetModule(),
				new OntologyModule(), new AnalyzerModule(),
				new GateModule(), new LatexParserModule(), new NlpModule(),
				new PdfParserModule());
		return injector;
	}

	@Override
	public ParseResult getParse(Content content) {
		String baseUrl = content.getBaseUrl();
		String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
		try {
			String result = domainAdapter.handle(mathnetKey);
			return ParseResult.createParseResult(content.getUrl(),
					new ParseImpl(result, new ParseData(
							ParseStatus.STATUS_SUCCESS, "", NO_OUTLINKS,
							new Metadata())));
		} catch (Throwable e) {
			System.out.println(String.format(
					"Failed to parse the content of a document=%s due to: %s",
					baseUrl, e));
		}
		return null;
	}

}
