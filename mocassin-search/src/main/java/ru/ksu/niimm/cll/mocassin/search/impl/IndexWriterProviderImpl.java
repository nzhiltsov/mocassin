package ru.ksu.niimm.cll.mocassin.search.impl;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import ru.ksu.niimm.cll.mocassin.search.providers.IndexWriterProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class IndexWriterProviderImpl implements
		IndexWriterProvider<IndexWriter> {

	private Directory directory;

	@Inject
	public IndexWriterProviderImpl(
			@Named("lucene.directory") Directory directory) {
		this.directory = directory;
	}

	@Override
	public IndexWriter get() throws Exception {

		IndexWriter indexWriter = new IndexWriter(this.directory,
				new IndexWriterConfig(Version.LUCENE_31, new StandardAnalyzer(
						Version.LUCENE_31)));
		return indexWriter;
	}
}
