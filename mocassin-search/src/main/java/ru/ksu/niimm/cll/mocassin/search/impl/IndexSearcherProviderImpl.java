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

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

import ru.ksu.niimm.cll.mocassin.search.FullTextModule;
import ru.ksu.niimm.cll.mocassin.search.providers.IndexSearcherProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Configures the index searcher, which searches in a wired index directory
 * 
 * @see FullTextModule#configure(com.google.inject.Binder)
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class IndexSearcherProviderImpl implements
	IndexSearcherProvider<IndexSearcher> {
    private Directory directory;

    @Inject
    private IndexSearcherProviderImpl(
	    @Named("lucene.directory") Directory directory) {
	this.directory = directory;
    }

    /**
     * @returns an index searcher instance that searches in a wired index
     *          directory
     * 
     */
    @Override
    public IndexSearcher get() throws IOException {
	IndexSearcher indexSearcher = new IndexSearcher(this.directory);
	return indexSearcher;
    }

}
