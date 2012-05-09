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
package ru.ksu.niimm.cll.mocassin.crawl.arxiv;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * The module configures facilities to work with ArXiv.org API
 * 
 * It uses <strong>arxiv-module.properties</strong> file from the classpath, in
 * particular, to set up a connection properties.
 * 
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class ArxivModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
	try {
	    Properties properties = new Properties();
	    properties.load(this.getClass().getClassLoader()
		    .getResourceAsStream("arxiv-module.properties"));
	    Names.bindProperties(binder(), properties);
	} catch (IOException ex) {
	    throw new RuntimeException(
		    "Failed to load the Arxiv module configuration");
	}
	bind(ArxivDAOFacade.class).to(ArxivDAOFacadeImpl.class);
	bindListener(Matchers.any(), new Slf4jTypeListener());
    }
}
