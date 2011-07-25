package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.ArxmlivProducer;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.impl.ArxmlivProducerImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.LatexDocumentDAOImpl;
import ru.ksu.niimm.cll.mocassin.parser.pdf.LatexDocumentShadedPatcher;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class LatexParserModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("parser_module.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Parser module configuration");
		}
		bind(Parser.class).to(LatexParserImpl.class);
		bind(StructureBuilder.class).to(StructureBuilderImpl.class);
		
		bind(LatexDocumentDAO.class).to(LatexDocumentDAOImpl.class);
		bind(LatexDocumentHeaderPatcher.class).to(SedBasedHeaderPatcher.class);
		bind(ArxmlivProducer.class).to(ArxmlivProducerImpl.class);
		
		
		
	}

}
