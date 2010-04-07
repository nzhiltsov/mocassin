package unittest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public class InsertQueryGeneratorTest {
	private static final String TRIPLE_2 = "<all.omdoc#whatislogic> <http://salt.semanticauthoring.org/onto/abstract-document-ontology#hasPart> <all.omdoc#whatislogic.p11>";
	private static final String TRIPLE_1 = "<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory>";
	private static final String MOCASSIN_TEST_URL = "jdbc:virtuoso://192.168.2.143:1111";
	private static final String MOCASSIN_TEST_PASSWORD = "mocassintest";
	private static final String MOCASSIN_TEST_USERNAME = "mocassinuser";
	private static final String MOCASSIN_TEST_GRAPH_IRI = "<http://cll.niimm.ksu.ru/mocassintest>";

	@Inject
	private InsertQueryGenerator insertQueryGenerator;

	@Test
	public void testGenerate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(TRIPLE_1);
		RDFTriple triple2 = new RDFTripleImpl(TRIPLE_2);
		triples.add(triple);
		triples.add(triple2);
		RDFGraph graph = new RDFGraphImpl.Builder(MOCASSIN_TEST_GRAPH_IRI)
				.username(MOCASSIN_TEST_USERNAME).password(
						MOCASSIN_TEST_PASSWORD).url(MOCASSIN_TEST_URL).build();
		String expression = getInsertQueryGenerator().generate(triples, graph);
		Assert.assertTrue(expression.equalsIgnoreCase(String.format(
				"INSERT INTO GRAPH %s {%s .%s .}", MOCASSIN_TEST_GRAPH_IRI,
				TRIPLE_1, TRIPLE_2)));
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

}
