package ru.ksu.niimm.cll.mocassin.parser;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.parser.impl.LatexDocumentDAOImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.NumberingProcessor;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.NumberingProcessorImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.StructureBuilderImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.LatexParserImpl;
import ru.ksu.niimm.cll.mocassin.parser.latex.impl.PdfsyncMapper;
import ru.ksu.niimm.cll.mocassin.parser.util.LatexDocumentHeaderPatcher;
import ru.ksu.niimm.cll.mocassin.parser.util.SedCommandPatcher;

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
		bind(NumberingProcessor.class).to(NumberingProcessorImpl.class);
		bind(Latex2PDFMapper.class).to(PdfsyncMapper.class);
		bind(LatexDocumentDAO.class).to(LatexDocumentDAOImpl.class);
		bind(LatexDocumentHeaderPatcher.class).to(SedCommandPatcher.class);
	}

}
