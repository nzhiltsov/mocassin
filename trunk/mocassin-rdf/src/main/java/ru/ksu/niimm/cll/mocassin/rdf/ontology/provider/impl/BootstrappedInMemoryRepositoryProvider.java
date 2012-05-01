/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.impl;

import java.io.InputStream;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.provider.RepositoryProvider;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

public class BootstrappedInMemoryRepositoryProvider implements
		RepositoryProvider<Repository> {
	@InjectLogger
	private Logger logger;

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
				connection.add(inputStream, context, RDFFormat.RDFXML,
						repository.getValueFactory().createURI(context));
			} finally {
				inputStream.close();
			}

		} catch (Exception ex) {
			logger.error("Failed to load the bootstrap data", ex);
			throw new RuntimeException(ex);
		} finally {
			connection.close();
		}
		logger.debug("The bootstrap data have been loaded successfully.");
		return repository;
	}

}
