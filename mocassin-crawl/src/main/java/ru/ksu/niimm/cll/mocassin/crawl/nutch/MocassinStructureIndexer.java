package ru.ksu.niimm.cll.mocassin.crawl.nutch;

import static java.lang.String.format;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.nutch.crawl.CrawlDatum;
import org.apache.nutch.crawl.Inlinks;
import org.apache.nutch.indexer.IndexingException;
import org.apache.nutch.indexer.IndexingFilter;
import org.apache.nutch.indexer.NutchDocument;
import org.apache.nutch.metadata.Metadata;
import org.apache.nutch.parse.Outlink;
import org.apache.nutch.parse.Parse;
import org.apache.nutch.parse.ParseData;
import org.apache.nutch.parse.ParseImpl;
import org.apache.nutch.parse.ParseResult;
import org.apache.nutch.parse.ParseStatus;
import org.apache.nutch.parse.Parser;
import org.apache.nutch.protocol.Content;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.ntriples.NTriplesWriterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.crawl.mathnet.MathnetModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyModule;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.Link;
import ru.ksu.niimm.cll.mocassin.util.model.Link.PdfLinkPredicate;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.uci.ics.jung.graph.Graph;

public class MocassinStructureIndexer implements IndexingFilter {
    private static final Logger LOG = LoggerFactory
	    .getLogger(MocassinStructureIndexer.class);
    protected static final Outlink[] NO_OUTLINKS = new Outlink[0];

    private static final String ID_PATTERN = "%s%s";
    private static final String MATHNET_PREFIX = "http://mathnet.ru/";
    /**
     * TODO: move to a configuration file
     */
    private static final String MATHNET_DOWNLOAD_PDF_URL = "http://www.mathnet.ru/php/getFT.phtml?jrnid=%s&paperid=%s&what=fullt&option_lang=rus";

    private Configuration conf;

    private ReferenceSearcher referenceSearcher;

    private ReferenceStatementGenerator referenceStatementGenerator;

    public Configuration getConf() {
	return conf;
    }

    private static Injector createInjector() {
	Injector injector = Guice
		.createInjector(new OntologyModule(), new AnalyzerModule(),
			new GateModule(), new LatexParserModule(),
			new NlpModule(), new PdfParserModule());
	return injector;
    }

    public void setConf(Configuration conf) {
	this.conf = conf;
	Injector injector = createInjector();
	referenceSearcher = injector.getInstance(ReferenceSearcher.class);
	referenceStatementGenerator = injector
		.getInstance(ReferenceStatementGenerator.class);
    }
    
    

    @Override
    public NutchDocument filter(NutchDocument doc, Parse parse, Text url,
	    CrawlDatum datum, Inlinks inlinks) throws IndexingException {
	String baseUrl = url.toString();
	String mathnetKey = StringUtil.extractMathnetKeyFromFilename(baseUrl);
	ArticleMetadata metadata = createMetadata(mathnetKey);
	ParsedDocument document = createParsedDocument(metadata);
	try {

	    Graph<StructuralElement, Reference> graph = referenceSearcher
		    .retrieveStructuralGraph(document);
	    List<Statement> triples = referenceStatementGenerator
		    .convert(graph);
	    String strTriples = asString(triples);
	    doc.add("rdf", strTriples);
	    return doc;
	} catch (Throwable e) {
	    LOG.error("Failed to parse a document={}.", mathnetKey, e);
	    throw new IndexingException();
	}
    }


    private String asString(List<Statement> triples) throws RDFHandlerException {
	NTriplesWriterFactory factory = new NTriplesWriterFactory();
	StringWriter sw = new StringWriter();
	RDFWriter writer = factory.getWriter(sw);
	writer.startRDF();
	for (Statement statement : triples) {
	writer.handleStatement(statement);
	}
	writer.endRDF();
	return sw.toString();
    }

    private static ParsedDocument createParsedDocument(ArticleMetadata metadata) {
	Link pdfLink = Iterables.find(metadata.getLinks(),
		new PdfLinkPredicate());
	ParsedDocument document = new ParsedDocumentImpl(
		metadata.getCollectionId(), metadata.getId(), pdfLink.getHref());
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
