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
import gate.creole.ANNIEConstants;
import gate.creole.SerialAnalyserController;
import gate.util.persistence.PersistenceManager;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AnnieControllerProviderImpl implements
		AnnieControllerProvider<SerialAnalyserController> {
	private final String CONFIG_FILE_NAME;

	@Inject
	public AnnieControllerProviderImpl(
			@Named("gate.loaded.plugin.config") String configName) {
		CONFIG_FILE_NAME = configName;
	}

	@Override
	public SerialAnalyserController get()
			throws AnnieControllerCreationException {
		SerialAnalyserController annie;
		try {
			annie = (SerialAnalyserController) PersistenceManager
					.loadObjectFromFile(new File(new File(
							Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
							CONFIG_FILE_NAME));
		} catch (Exception e) {
			throw new AnnieControllerCreationException(e);
		}
		return annie;
	}
}
