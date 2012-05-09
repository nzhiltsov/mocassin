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
package ru.ksu.niimm.cll.mocassin.search;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.name.Names;

/**
 * The class configures facilities for full text indexing
 * 
 * <p>
 * It uses <strong>fulltext-module.properties</strong> file from the classpath,
 * in particular, mandatory <i>'lucene.directory'</i> property, which must be defined there.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class FullTextModule extends AbstractModule {
    private static final String LUCENE_DIRECTORY_PARAMETER_NAME = "lucene.directory";
    @Inject
    private Logger logger;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
	bindDirectory();
    }

    /**
     * Configures directory to persist the index
     */
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
