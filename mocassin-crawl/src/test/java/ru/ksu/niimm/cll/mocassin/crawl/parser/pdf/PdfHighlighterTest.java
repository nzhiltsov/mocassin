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

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ PdfParserModule.class })
public class PdfHighlighterTest {
	@Inject
	private PdfHighlighter pdfHighlighter;

	@Test
	public void testGenerate() throws PdflatexCompilationException {
		pdfHighlighter.generateHighlightedPdf("ivm537", 1082, 240, 305);
		Assert.assertTrue(new File(String.format("/opt/mocassin/pdf/%s$%d.pdf",
				"ivm537", 1082)).exists());
	}

}
