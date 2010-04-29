package unittest;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;

import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

import unittest.util.LoadPropertiesUtil;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public abstract class AbstractTest {
	private static Properties properties;

	@BeforeClass
	public static void init() throws Exception {
		properties = LoadPropertiesUtil.loadProperties();
	}

	public Properties getProperties() {
		return properties;
	}
}
