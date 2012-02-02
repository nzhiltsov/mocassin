package ru.ksu.niimm.cll.mocassin.ontology.provider.impl;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import ru.ksu.niimm.cll.mocassin.ontology.provider.RepositoryProvider;
import virtuoso.sesame2.driver.VirtuosoRepository;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class VirtuosoRepositoryProvider implements
		RepositoryProvider<Repository> {
	private final String connectionUrl;

	private final String username;

	private final String password;

	@Inject
	public VirtuosoRepositoryProvider(
			@Named("connection.url") String connectionUrl,
			@Named("connection.user.name") String username,
			@Named("connection.user.password") String password) {
		this.connectionUrl = connectionUrl;
		this.username = username;
		this.password = password;
	}

	@Override
	public Repository get() throws RepositoryException {
		Repository repository = new VirtuosoRepository(connectionUrl, username,
				password);
		repository.initialize();
		return repository;
	}

}
