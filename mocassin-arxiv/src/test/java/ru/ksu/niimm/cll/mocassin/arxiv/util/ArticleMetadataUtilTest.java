package ru.ksu.niimm.cll.mocassin.arxiv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.util.ArticleMetadataReader;

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
