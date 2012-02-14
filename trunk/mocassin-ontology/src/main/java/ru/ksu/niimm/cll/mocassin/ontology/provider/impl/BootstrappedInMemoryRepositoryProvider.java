package ru.ksu.niimm.cll.mocassin.ontology.provider.impl;

import static java.lang.String.format;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;

@Singleton
public class BootstrappedInMemoryRepositoryProvider implements
		RepositoryProvider<Repository> {

	private final Logger logger;

	@Inject
	public BootstrappedInMemoryRepositoryProvider(Logger logger) {
		this.logger = logger;
	}

	@Override
	public Repository get() throws RepositoryException {
		Repository repository = new SailRepository(new MemoryStore());
		repository.initialize();
		final String context = "http://cll.niimm.ksu.ru/mocassinfortest";
		RepositoryConnection connection = repository.getConnection();
		try {
			InputStream inputStream = BootstrappedInMemoryRepositoryProvider.class
					.getClassLoader().getResourceAsStream("bootstrapdata.rdf");
			try {
				connection
						.add(inputStream, context,
								RDFFormat.RDFXML,repository.getValueFactory().createURI(
										context));
			} finally {
				inputStream.close();
			}
			
		} catch (Exception ex) {
			logger.log(Level.SEVERE,
					format("Failed to load the bootstrap data due to %s.", ex));
			throw new RuntimeException(ex);
		} finally {
			connection.close();
		}
		logger.log(Level.INFO,
				"The bootstrap data have been loaded successfully.");
		return repository;
	}

}
