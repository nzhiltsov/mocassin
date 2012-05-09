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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import gate.Gate;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

/**
 * This class configures facilities for NLP via GATE machinery.
 * 
 * <p>It uses the <strong>nlp-module.properties</strong> file from the classpath,
 * which shouldn't be edited in general.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class GateModule extends AbstractModule {

    private static final String GATE_SITE_CONFIG_CONF_PROPERTY = "gate.site.config";
    private static final String GATE_BUILTIN_CREOLE_DIR_CONF_PROPERTY = "gate.builtin.creole.dir";
    private static final String GATE_PLUGINS_HOME_CONF_PROPERTY = "gate.plugins.home";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
	Properties properties = new Properties();
	try {
	    properties.load(this.getClass().getClassLoader()
		    .getResourceAsStream("nlp-module.properties"));
	    Names.bindProperties(binder(), properties);
	} catch (IOException ex) {
	    throw new RuntimeException(
		    "Failed to load the GATE module configuration.");
	}

	try {
	    URI pluginsHome = this
		    .getClass()
		    .getResource(
			    "/"
				    + properties
					    .getProperty(GATE_PLUGINS_HOME_CONF_PROPERTY))
		    .toURI();
	    Gate.setPluginsHome(new File(pluginsHome));
	    URI siteConfig = this
		    .getClass()
		    .getResource(
			    "/"
				    + properties
					    .getProperty(GATE_SITE_CONFIG_CONF_PROPERTY))
		    .toURI();
	    Gate.setSiteConfigFile(new File(siteConfig));
	    URI builtinCreoleDir = this
		    .getClass()
		    .getResource(
			    "/"
				    + properties
					    .getProperty(GATE_BUILTIN_CREOLE_DIR_CONF_PROPERTY))
		    .toURI();
	    Gate.setBuiltinCreoleDir(builtinCreoleDir.toURL());
	    Gate.init();
	} catch (GateException e) {
	    throw new RuntimeException("Failed to initialize GATE.", e);
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Failed to initialize GATE.", e);
	} catch (MalformedURLException e) {
	    throw new RuntimeException("Failed to initialize GATE.", e);
	}
	bind(GateProcessingFacade.class).to(AnnieBasedGateProcessingFacade.class);
	bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
	bindListener(Matchers.any(), new Slf4jTypeListener());
	ThrowingProviderBinder
		.create(binder())
		.bind(AnnieControllerProvider.class,
			SerialAnalyserController.class)
		.to(AnnieControllerProviderImpl.class).in(Singleton.class);
    }

}
