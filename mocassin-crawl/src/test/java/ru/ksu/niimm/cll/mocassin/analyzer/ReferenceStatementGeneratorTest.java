package ru.ksu.niimm.cll.mocassin.analyzer;


import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.SECTION;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.THEOREM;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses.getUri;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations.HAS_PART;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.model.URIConstants.createRdfTypeTriple;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.model.URIConstants.createTriple;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.AnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceSearcher;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.ReferenceStatementGenerator;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateDocumentException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.AccessGateStorageException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.NlpModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ProcessException;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;
import ru.ksu.niimm.cll.mocassin.fulltext.FullTextModule;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import edu.uci.ics.jung.graph.Graph;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ AnalyzerModule.class, NlpModule.class, LatexParserModule.class,
		OntologyTestModule.class, FullTextModule.class,
		GateModule.class, PdfParserModule.class })
public class ReferenceStatementGeneratorTest {
	@Inject
	protected ReferenceStatementGenerator referenceStatementGenerator;
	@Inject
	private ReferenceSearcher referenceSearcher;
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private Graph<StructuralElement, Reference> graph;

	@Before
	public void init() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		ParsedDocument document = new ParsedDocumentImpl("ivm18",
				"http://mathnet.ru/ivm18", "http://mathnet.ru/ivm18");
		gateProcessingFacade.process(document.getCollectionId());
		graph = this.referenceSearcher.retrieveStructuralGraph(document);
		Collection<Reference> edges = graph.getEdges();
		Assert.assertTrue("The reference list is empty", edges.size() > 0);
	}

	@Test
	public void testConvert() {
		List<Statement> statements = referenceStatementGenerator.convert(graph);
		final String sectionInstance = "http://mathnet.ru/ivm18/1017";
		final String theoremInstance = "http://mathnet.ru/ivm18/2900";
		Statement hasPartStatement = createTriple(sectionInstance,
				HAS_PART.getUri(), theoremInstance);
		Assert.assertTrue(
				"Generated statement list does contain the 'hasPart' statement.",
				statements.contains(hasPartStatement));
		Statement hasSectionInstanceType = createRdfTypeTriple(sectionInstance,
				getUri(SECTION));
		Assert.assertTrue(
				"Generated statement list does contain any statement about the section instance type.",
				statements.contains(hasSectionInstanceType));
		Statement hasTheoremInstanceType = createRdfTypeTriple(theoremInstance,
				getUri(THEOREM));
		Assert.assertTrue(
				"Generated statement list does contain any statement about the theorem instance type.",
				statements.contains(hasTheoremInstanceType));
	}
}
