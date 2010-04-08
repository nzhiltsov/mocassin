package unittest;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;
import unittest.util.LoadPropertiesUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public class VirtuosoDAOTest {

	@Inject
	private VirtuosoDAO virtuosoDAO;

	private static Properties properties;

	@BeforeClass
	public static void init() throws Exception {
		properties = LoadPropertiesUtil.loadProperties();
	}

	@Test
	public void testInsert() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(
				"<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory>");
		triples.add(triple);
		RDFGraph graph = new RDFGraphImpl.Builder(getProperties().getProperty(
				"graph.iri")).username(
				getProperties().getProperty("connection.user.name")).password(
				getProperties().getProperty("connection.user.password")).url(
				getProperties().getProperty("connection.url")).build();

		getVirtuosoDAO().insert(triples, graph);
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

	public Properties getProperties() {
		return properties;
	}

}
