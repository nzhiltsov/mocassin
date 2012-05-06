package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import gate.Gate;
import gate.util.GateException;

import java.io.File;

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

import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateDocumentDAO;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinGateParser implements Parser {
    private static final Logger LOG = LoggerFactory
	    .getLogger(MocassinGateParser.class);
    private static final String GATE_DOCUMENT_ENCODING = "utf8";

    private Configuration conf;

    private GateDocumentDAO gateDocumentDAO;

    private GateProcessingFacade gateProcessingFacade;

    private ArxmlivProducer arxmlivProducer;

    public Configuration getConf() {
	return conf;
    }

    private static Injector createInjector() {
	Injector injector = Guice.createInjector(new LatexParserModule(),
		new PdfParserModule(), new GateModule());
	return injector;
    }

    public void setConf(Configuration conf) {
	this.conf = conf;
	Injector injector = createInjector();
	arxmlivProducer = injector.getInstance(ArxmlivProducer.class);
	gateDocumentDAO = injector.getInstance(GateDocumentDAO.class);
	gateProcessingFacade = injector.getInstance(GateProcessingFacade.class);
    }

    @Override
    public ParseResult getParse(Content content) {
	String baseUrl = content.getBaseUrl();
	String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
	String arxmlivDocFilePath = String.format("%s/%s",
		arxmlivProducer.getArxmlivDocumentDirectory(),
		StringUtil.arxivid2filename(mathnetKey, "tex.xml"));
	try {
	    gateDocumentDAO.save(mathnetKey, new File(arxmlivDocFilePath),
		    GATE_DOCUMENT_ENCODING);
	    gateProcessingFacade.process(mathnetKey);
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	    return new ParseStatus(ParseStatus.FAILED, "").getEmptyParseResult(
		    content.getUrl(), getConf());
	}
	return null;
    }

}