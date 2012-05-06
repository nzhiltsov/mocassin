package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.Latex2PDFMapper;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdflatexWrapper;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinPdflatexParser implements Parser {
    private static final Logger LOG = LoggerFactory
	    .getLogger(MocassinPdflatexParser.class);
    protected static final Outlink[] NO_OUTLINKS = new Outlink[0];

    private PdflatexWrapper pdflatexWrapper;

    private Latex2PDFMapper latex2pdfMapper;

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
	pdflatexWrapper = injector.getInstance(PdflatexWrapper.class);
	latex2pdfMapper = injector.getInstance(Latex2PDFMapper.class);
    }

    @Override
    public ParseResult getParse(Content content) {
	String baseUrl = content.getBaseUrl();
	String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
	try {
	    pdflatexWrapper.compilePatched(mathnetKey);
	    latex2pdfMapper.generateSummary(mathnetKey);
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	    return new ParseStatus(ParseStatus.FAILED, "").getEmptyParseResult(
		    content.getUrl(), getConf());
	}
	return null;
    }

}
