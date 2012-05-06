package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import static java.lang.String.format;
import edu.uci.ics.jung.graph.Graph;
import gate.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Link;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinStructureParser implements Parser {
    private static final Logger LOG = LoggerFactory
	    .getLogger(MocassinStructureParser.class);
    private static final Outlink[] NO_OUTLINKS = new Outlink[0];
    private static final String GATE_DOCUMENT_ENCODING = "utf8";

    private static final String ID_PATTERN = "%s%s";
    private static final String MATHNET_PREFIX = "http://mathnet.ru/";
    /**
     * TODO: move to a configuration file
     */
    private static final String MATHNET_DOWNLOAD_PDF_URL = "http://www.mathnet.ru/php/getFT.phtml?jrnid=%s&paperid=%s&what=fullt&option_lang=rus";

    private Configuration conf;

    private ReferenceSearcher referenceSearcher;

    private ReferenceStatementGenerator referenceStatementGenerator;

    private OntologyResourceFacade ontologyResourceFacade;

    private GateProcessingFacade gateProcessingFacade;

    private ArxmlivProducer arxmlivProducer;

    public Configuration getConf() {
	return conf;
    }

    protected static Injector createInjector() {
	Injector injector = Guice
		.createInjector(new OntologyModule(), new AnalyzerModule(),
			new GateModule(), new LatexParserModule(),
			new NlpModule(), new PdfParserModule());
	return injector;
    }

    public void setConf(Configuration conf) {
	this.conf = conf;
	Injector injector = createInjector();
	arxmlivProducer = injector.getInstance(ArxmlivProducer.class);
	gateProcessingFacade = injector.getInstance(GateProcessingFacade.class);
	referenceSearcher = injector.getInstance(ReferenceSearcher.class);
	referenceStatementGenerator = injector
		.getInstance(ReferenceStatementGenerator.class);
	ontologyResourceFacade = injector
		.getInstance(OntologyResourceFacade.class);
    }

    @Override
    public ParseResult getParse(Content content) {
	String baseUrl = content.getBaseUrl();
	String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
	ArticleMetadata metadata = createMetadata(mathnetKey);
	String arxmlivDocFilePath = String.format("%s/%s",
		arxmlivProducer.getArxmlivDocumentDirectory(),
		StringUtil.arxivid2filename(mathnetKey, "tex.xml"));
	try {
	    Document document = gateProcessingFacade.process(mathnetKey,
		    new File(arxmlivDocFilePath), GATE_DOCUMENT_ENCODING);
	    Graph<StructuralElement, Reference> graph = referenceSearcher
		    .retrieveStructuralGraph(document, metadata.getId());
	    List<Statement> triples = referenceStatementGenerator
		    .convert(graph);
	    if (!ontologyResourceFacade.insert(triples)) {
		throw new RuntimeException("Failed to insert triples.");
	    }
	    return ParseResult.createParseResult(content.getUrl(),
		    new ParseImpl("", new ParseData(ParseStatus.STATUS_SUCCESS,
			    "", NO_OUTLINKS, new Metadata())));
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	    return new ParseStatus(ParseStatus.FAILED, "").getEmptyParseResult(
		    content.getUrl(), getConf());
	}
    }

    private static ParsedDocument createParsedDocument(ArticleMetadata metadata) {
	ParsedDocument document = new ParsedDocumentImpl(metadata.getId());
	return document;
    }

    private static ArticleMetadata createMetadata(String mathnetKey) {
	ArticleMetadata metadata = new ArticleMetadata();
	metadata.setId(format(ID_PATTERN, MATHNET_PREFIX, mathnetKey));
	metadata.setCollectionId(mathnetKey);
	ArrayList<Link> links = new ArrayList<Link>();

	links.add(Link.pdfLink(String.format(MATHNET_DOWNLOAD_PDF_URL,
		StringUtil.extractJournalPrefixFromMathnetKey(mathnetKey),
		StringUtil.extractPaperNumberFromMathnetKey(mathnetKey))));
	metadata.setLinks(links);
	return metadata;
    }

}
