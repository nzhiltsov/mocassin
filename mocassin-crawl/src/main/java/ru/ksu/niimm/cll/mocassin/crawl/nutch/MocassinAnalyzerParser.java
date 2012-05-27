package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import static java.lang.String.format;
import edu.uci.ics.jung.graph.AbstractGraph;
import edu.uci.ics.jung.graph.Graph;
import gate.Document;
import gate.Factory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.DocumentAnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.GraphTopologyAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.RelationFeatureInfo;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Link;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class MocassinAnalyzerParser implements Parser {
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

    private GateProcessingFacade gateProcessingFacade;

    private ArxmlivProducer arxmlivProducer;

    private GraphTopologyAnalyzer graphTopologyAnalyzer;

    public Configuration getConf() {
	return conf;
    }

    protected Injector createInjector() {
	Injector injector = Guice.createInjector(new DocumentAnalyzerModule(),
		new GateModule(), new LatexParserModule(),
		new PdfParserModule());
	return injector;
    }

    public void setConf(Configuration conf) {
	this.conf = conf;
	Injector injector = createInjector();
	arxmlivProducer = injector.getInstance(ArxmlivProducer.class);
	gateProcessingFacade = injector.getInstance(GateProcessingFacade.class);
	referenceSearcher = injector.getInstance(ReferenceSearcher.class);
	graphTopologyAnalyzer = injector
		.getInstance(GraphTopologyAnalyzer.class);
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
	    Graph<StructuralElement, Reference> graph;
	    try {
		graph = referenceSearcher.retrieveStructuralGraph(document,
			metadata.getId());
	    } finally {
		Factory.deleteResource(document);
	    }

	    List<RelationFeatureInfo> candidates = graphTopologyAnalyzer
		    .extractCandidateFeatures(graph);
	    saveCandidates(mathnetKey, graph, candidates);
	    return ParseResult.createParseResult(content.getUrl(),
		    new ParseImpl("", new ParseData(ParseStatus.STATUS_SUCCESS,
			    "", NO_OUTLINKS, new Metadata())));
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	    return new ParseStatus(ParseStatus.FAILED, "").getEmptyParseResult(
		    content.getUrl(), getConf());
	}
    }

    private synchronized void saveCandidates(String mathnetKey,
	    Graph<StructuralElement, Reference> graph,
	    List<RelationFeatureInfo> candidates) throws IOException {
	FileWriter fw = new FileWriter("/opt/mocassin/stats/" + mathnetKey
		+ ".csv");
	try {
	    for (RelationFeatureInfo candidate : candidates) {
		fw.write(format("%s %s", mathnetKey, candidate.toString()));
		fw.write('\n');
	    }
	    fw.flush();
	} finally {
	    fw.close();
	}
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
