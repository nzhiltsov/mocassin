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
