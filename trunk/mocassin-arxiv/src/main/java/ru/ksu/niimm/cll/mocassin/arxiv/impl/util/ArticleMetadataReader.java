package ru.ksu.niimm.cll.mocassin.arxiv.impl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.thoughtworks.xstream.XStream;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Feed;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.FeedXStream;

public class ArticleMetadataReader {
	private ArticleMetadataReader() {
	}

	public static ArticleMetadata read(InputStream inputStream)
			throws IOException, ClassNotFoundException {
		XStream xstream = new FeedXStream();

		Feed feed = (Feed) xstream.fromXML(inputStream);

		return feed != null ? feed.getEntry() : null;
	}
}
