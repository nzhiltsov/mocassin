package unittest.util;

import java.io.IOException;

import ru.ksu.niimm.ose.ontology.loader.impl.ModulePropertiesLoaderImpl;

public class ModulePropertiesLoaderTestImpl extends ModulePropertiesLoaderImpl {

	private static final String TEST_CONFIG_PROPERTIES_FILENAME = "test_ontology_config.properties";

	public ModulePropertiesLoaderTestImpl() throws IOException {
		super();
	}

	@Override
	protected String getConfigPropertiesFilename() {
		return TEST_CONFIG_PROPERTIES_FILENAME;
	}

}
