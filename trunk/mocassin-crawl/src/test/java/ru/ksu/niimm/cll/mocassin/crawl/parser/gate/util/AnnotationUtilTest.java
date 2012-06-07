package ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.DocumentAnalyzerModule;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Term;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.GateProcessingFacade;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.crawl.parser.pdf.PdfParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class, LatexParserModule.class,
	PdfParserModule.class, DocumentAnalyzerModule.class })
public class AnnotationUtilTest {
    @Inject
    private AnnotationUtil annotationUtil;
    @Inject
    private GateProcessingFacade gateProcessingFacade;

    private Document document;

    @Before
    public void init() {
	this.document = gateProcessingFacade.process("ivm18", new File(
		"/opt/mocassin/arxmliv/ivm18.tex.xml"), "utf8");
    }

    @After
    public void shutdown() {
	if (this.document != null) {
	    Factory.deleteResource(document);
	}
    }

    @Test
    public void testGetTerms() {
	AnnotationSet annotations = document.getAnnotations("Original markups")
		.get("section");
	Assert.assertTrue("Not found section annotations.",
		annotations.size() > 0);
	Annotation foundAnnotation = annotations.iterator().next();
	float confidenceThreshold = 1;
	List<Term> terms = annotationUtil.getTerms("http://mathnet.ru/ivm18", document, foundAnnotation, confidenceThreshold);
	for (Term term : terms) {
	    if(term.getMathExpressions().size() > 0) {
		break;
	    }
	}
    }
}
