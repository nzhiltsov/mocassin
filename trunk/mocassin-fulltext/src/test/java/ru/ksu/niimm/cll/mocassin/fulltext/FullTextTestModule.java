package ru.ksu.niimm.cll.mocassin.fulltext;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.google.inject.name.Names;

public class FullTextTestModule extends FullTextModule {

	@Override
	protected void bindDirectory() {
		bind(Directory.class).annotatedWith(Names.named("lucene.directory"))
				.toInstance(new RAMDirectory());
	}

}
