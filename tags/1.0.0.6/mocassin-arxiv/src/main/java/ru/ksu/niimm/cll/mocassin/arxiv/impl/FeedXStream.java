package ru.ksu.niimm.cll.mocassin.arxiv.impl;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class FeedXStream extends XStream {

	public FeedXStream() {
		super(new DomDriver());
		alias("feed", Feed.class);
		alias("entry", ArticleMetadata.class);
		alias("author", Author.class);
		alias("link", Link.class);
		useAttributeFor(Link.class, "href");
		useAttributeFor(Link.class, "type");
		addImplicitCollection(ArticleMetadata.class, "authors", Author.class);
		addImplicitCollection(ArticleMetadata.class, "links", Link.class);
	}

	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
		return new MapperWrapper(next) {
			public boolean shouldSerializeMember(Class definedIn,
					String fieldName) {
				try {
					return definedIn != Object.class
							|| realClass(fieldName) != null;
				} catch (CannotResolveClassException cnrce) {
					return false;
				}
			}
		};
	}

}
