package ru.ksu.niimm.cll.mocassin.search.impl;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

import ru.ksu.niimm.cll.mocassin.search.providers.IndexSearcherProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class IndexSearcherProviderImpl implements
		IndexSearcherProvider<IndexSearcher> {
	private Directory directory;

	@Inject
	public IndexSearcherProviderImpl(
			@Named("lucene.directory") Directory directory) {
		this.directory = directory;
	}

	@Override
	public IndexSearcher get() throws IOException {
		IndexSearcher indexSearcher = new IndexSearcher(this.directory);
		return indexSearcher;
	}

}
