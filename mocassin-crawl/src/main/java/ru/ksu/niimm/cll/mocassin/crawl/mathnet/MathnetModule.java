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
package ru.ksu.niimm.cll.mocassin.crawl.mathnet;

import ru.ksu.niimm.cll.mocassin.crawl.DomainAdapter;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class MathnetModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DomainAdapter.class).to(MathnetAdapter.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
