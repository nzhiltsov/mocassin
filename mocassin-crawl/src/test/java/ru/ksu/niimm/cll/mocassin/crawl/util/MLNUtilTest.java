package ru.ksu.niimm.cll.mocassin.crawl.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation.MLNUtil;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyFacade;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyTestModule;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ OntologyTestModule.class })
public class MLNUtilTest {
    @Inject
    private OntologyFacade ontologyFacade;

    @Test
    public void testGenerateRules() throws IOException {
	MocassinOntologyRelations[] relations = {
		MocassinOntologyRelations.PROVES,
		MocassinOntologyRelations.DEPENDS_ON,
		MocassinOntologyRelations.EXEMPLIFIES,
		MocassinOntologyRelations.HAS_CONSEQUENCE,
		MocassinOntologyRelations.REFERS_TO };
	FileWriter fstream = new FileWriter("/tmp/rules.mln");
	BufferedWriter out = new BufferedWriter(fstream);

	for (MocassinOntologyRelations relation : relations) {
	    out.write(MLNUtil.generateDomainRangeRules(
		    ontologyFacade.getDomainClasses(relation),
		    ontologyFacade.getRangeClasses(relation), relation));
	}

	out.close();
    }
}
