package unittest;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import unittest.util.LoadPropertiesUtil;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public abstract class AbstractTest {
	private static Properties properties;
	private RDFGraph graph;

	public AbstractTest() {
		this.graph = new RDFGraphImpl.Builder(getProperties().getProperty(
				"graph.iri")).username(
				getProperties().getProperty("connection.user.name")).password(
				getProperties().getProperty("connection.user.password")).url(
				getProperties().getProperty("connection.url")).build();
	}

	@BeforeClass
	public static void init() throws Exception {
		properties = LoadPropertiesUtil.loadProperties();
	}

	public Properties getProperties() {
		return properties;
	}

	protected RDFGraph getGraph() {
		return graph;
	}

}
