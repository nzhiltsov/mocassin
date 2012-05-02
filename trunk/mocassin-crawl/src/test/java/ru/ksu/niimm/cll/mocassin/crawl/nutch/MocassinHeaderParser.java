package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import static java.lang.String.format;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinHeaderParser implements Parser {
    private static final Logger LOG = LoggerFactory
	    .getLogger(MocassinHeaderParser.class);

    private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;

    private Configuration conf;

    public Configuration getConf() {
	return conf;
    }

    private static Injector createInjector() {
	Injector injector = Guice.createInjector(new LatexParserModule(),
		new PdfParserModule());
	return injector;
    }

    public void setConf(Configuration conf) {
	this.conf = conf;
	Injector injector = createInjector();
	latexDocumentHeaderPatcher = injector
		.getInstance(LatexDocumentHeaderPatcher.class);
    }

    @Override
    public ParseResult getParse(Content content) {
	String baseUrl = content.getBaseUrl();
	String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
	try {
	    latexDocumentHeaderPatcher.patch(mathnetKey);
	    Outlink[] outlinks = { new Outlink(format(
		    "file:///opt/mocassin/patched-tex/%s.tex", mathnetKey),
		    null) };
	    return ParseResult.createParseResult(content.getUrl(),
		    new ParseImpl("", new ParseData(ParseStatus.STATUS_SUCCESS,
			    "", outlinks, new Metadata())));
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	}
	return null;
    }

}
