package ru.ksu.niimm.cll.mocassin.arxiv.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.util.ArticleMetadataReader;

public class ArticleMetadataUtilTest {
	@Test
	public void testRead() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		ArticleMetadata metadata = ArticleMetadataReader
				.read(new FileInputStream(
						"/OTHER_DATA/arxiv_papers/api_response_example.xml"));
		Assert.assertEquals("http://arxiv.org/abs/math/0205003v1", metadata
				.getId());
		Assert
				.assertEquals(
						"A strengthening of the Nyman-Beurling criterion for the Riemann\n  hypothesis, 2",
						metadata.getTitle());
	}
}
