package ru.ksu.niimm.cll.mocassin.fulltext.impl;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;

import ru.ksu.niimm.cll.mocassin.fulltext.IndexWriterProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
@Singleton
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
				new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
		return indexWriter;
	}

}
