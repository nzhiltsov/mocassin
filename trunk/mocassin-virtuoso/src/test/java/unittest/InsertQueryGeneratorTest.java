package unittest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;
import unittest.util.LoadPropertiesUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public class InsertQueryGeneratorTest {
	private static final String TRIPLE_2 = "<all.omdoc#whatislogic> <http://salt.semanticauthoring.org/onto/abstract-document-ontology#hasPart> <all.omdoc#whatislogic.p11>";
	private static final String TRIPLE_1 = "<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory>";

	@Inject
	private InsertQueryGenerator insertQueryGenerator;
	private static Properties properties;

	@BeforeClass
	public static void init() throws Exception {
		properties = LoadPropertiesUtil.loadProperties();
	}

	@Test
	public void testGenerate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(TRIPLE_1);
		RDFTriple triple2 = new RDFTripleImpl(TRIPLE_2);
		triples.add(triple);
		triples.add(triple2);
		RDFGraph graph = new RDFGraphImpl.Builder(getProperties().getProperty(
				"graph.iri")).username(
				getProperties().getProperty("connection.user.name")).password(
				getProperties().getProperty("connection.user.password")).url(
				getProperties().getProperty("connection.url")).build();
		String expression = getInsertQueryGenerator().generate(triples, graph);
		Assert.assertTrue(expression.equalsIgnoreCase(String.format(
				"INSERT INTO GRAPH %s {%s .%s .}", getProperties().getProperty(
						"graph.iri"), TRIPLE_1, TRIPLE_2)));
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

	public Properties getProperties() {
		return properties;
	}

}
