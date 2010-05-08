package unittest.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class LoadPropertiesUtil {

	private static final String CONFIG_PROPERTIES_FILENAME = "config.properties";

	private LoadPropertiesUtil() {
	}

	public static Properties loadProperties() throws Exception {
		Properties properties = new Properties();
		ClassLoader loader = LoadPropertiesUtil.class.getClassLoader();
		URL url = loader.getResource(CONFIG_PROPERTIES_FILENAME);
		InputStream stream = url.openStream();
		properties.load(stream);
		stream.close();
		return properties;
	}
}
