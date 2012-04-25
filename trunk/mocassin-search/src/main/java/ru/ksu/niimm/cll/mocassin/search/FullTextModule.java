package ru.ksu.niimm.cll.mocassin.search;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import ru.ksu.niimm.cll.mocassin.search.impl.PDFLuceneIndexer;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Names;

public class FullTextModule extends AbstractModule {
	private static final String LUCENE_DIRECTORY_PARAMETER_NAME = "lucene.directory";
	@Inject
	private Logger logger;

	@Override
	protected void configure() {

		bindDirectory();
		bindInjections();
	}

	protected void bindInjections() {
		bind(PDFIndexer.class).to(PDFLuceneIndexer.class);
	}

	protected void bindDirectory() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("fulltext-module.properties"));
			Names.bindProperties(binder(), properties);
			bind(Directory.class).annotatedWith(
					Names.named(LUCENE_DIRECTORY_PARAMETER_NAME)).toInstance(
					FSDirectory.open(new File(properties
							.getProperty(LUCENE_DIRECTORY_PARAMETER_NAME))));
		} catch (IOException e) {
			logger.log(
					Level.SEVERE,
					"failed to initialize the index directory due to: "
							+ e.getMessage());
			throw new RuntimeException();
		}
	}

}
