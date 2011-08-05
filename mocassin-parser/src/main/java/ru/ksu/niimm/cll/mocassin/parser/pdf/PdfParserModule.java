package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PdfParserModule extends AbstractModule {

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
		bind(PdfHighlighter.class).to(PdfHighlighterImpl.class);
		bind(PdflatexWrapper.class).to(PdflatexWrapperImpl.class);
		bind(Latex2PDFMapper.class).to(PdfsyncMapper.class);
		bind(LatexDocumentShadedPatcher.class).to(SedBasedShadedPatcher.class);
	}

}
