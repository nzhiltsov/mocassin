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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.util.model.ArticleMetadataReader;

import com.mycila.testing.junit.MycilaJunitRunner;

@RunWith(MycilaJunitRunner.class)
public class ArticleMetadataUtilTest {
	private InputStream in;

	@Before
	public void init() throws FileNotFoundException {
		this.in = new FileInputStream(new File(
				"src/test/resources/api_response_example.xml"));

	}

	@Test
	public void testRead() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		ArticleMetadata metadata = ArticleMetadataReader.read(this.in);
		Assert.assertEquals("http://arxiv.org/abs/math/0205003v1", metadata
				.getId());
		Assert
				.assertEquals(
						"A strengthening of the Nyman-Beurling criterion for the Riemann\n  hypothesis, 2",
						metadata.getTitle());
	}
}
