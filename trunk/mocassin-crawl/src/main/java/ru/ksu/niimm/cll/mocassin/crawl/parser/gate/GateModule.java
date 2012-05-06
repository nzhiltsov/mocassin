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

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtil;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.util.AnnotationUtilImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

public class GateModule extends AbstractModule {

    private static final String GATE_BUILTIN_CREOLE_DIR_CONF_PROPERTY = "gate.builtin.creole.dir";
    private static final String GATE_HOME_CONF_PROPERTY = "gate.home";

    @Override
    protected void configure() {
	Properties properties = new Properties();
	try {

	    properties.load(this.getClass().getClassLoader()
		    .getResourceAsStream("nlp_module.properties"));
	    Names.bindProperties(binder(), properties);
	} catch (IOException ex) {
	    throw new RuntimeException(
		    "Failed to load the GATE module configuration.");
	}
	System.setProperty(GATE_HOME_CONF_PROPERTY,
		properties.getProperty(GATE_HOME_CONF_PROPERTY));
	System.setProperty(GATE_BUILTIN_CREOLE_DIR_CONF_PROPERTY,
		properties.getProperty(GATE_BUILTIN_CREOLE_DIR_CONF_PROPERTY));
	try {
	    Gate.init();
	} catch (GateException e) {
	    throw new RuntimeException("Failed to initialize GATE.");
	}
	bind(GateProcessingFacade.class).to(GateProcessingFacadeImpl.class);
	bind(AnnotationUtil.class).to(AnnotationUtilImpl.class);
	bindListener(Matchers.any(), new Slf4jTypeListener());
	ThrowingProviderBinder
		.create(binder())
		.bind(AnnieControllerProvider.class,
			SerialAnalyserController.class)
		.to(AnnieControllerProviderImpl.class);
    }

}
