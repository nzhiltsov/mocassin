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
package ru.ksu.niimm.cll.mocassin.search.impl;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import ru.ksu.niimm.cll.mocassin.search.FullTextModule;
import ru.ksu.niimm.cll.mocassin.search.providers.IndexWriterProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Configures the index writer through using a wired index directory
 * 
 * @see FullTextModule#configure(com.google.inject.Binder)
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class IndexWriterProviderImpl implements
	IndexWriterProvider<IndexWriter> {

    private Directory directory;

    @Inject
    private IndexWriterProviderImpl(
	    @Named("lucene.directory") Directory directory) {
	this.directory = directory;
    }

    /**
     * @returns an index writer instance, configured through using a wired index
     *          directory
     *          
     */
    @Override
    public IndexWriter get() throws Exception {

	IndexWriter indexWriter = new IndexWriter(this.directory,
		new IndexWriterConfig(Version.LUCENE_31, new StandardAnalyzer(
			Version.LUCENE_31)));
	return indexWriter;
    }
}
