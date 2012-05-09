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
package ru.ksu.niimm.cll.mocassin.crawl.parser.pdf;

import java.io.IOException;
import java.util.Properties;

import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class PdfParserModule extends AbstractModule {

	@Override
	protected void configure() {
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader()
					.getResourceAsStream("parser-module.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException ex) {
			throw new RuntimeException(
					"failed to load the Parser module configuration");
		}
		bind(PdfHighlighter.class).to(PdfHighlighterImpl.class);
		bind(PdflatexWrapper.class).to(PdflatexWrapperImpl.class);
		bind(Latex2PDFMapper.class).to(PdfsyncMapper.class);
		bind(LatexDocumentShadedPatcher.class).to(SedBasedShadedPatcher.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}

}
