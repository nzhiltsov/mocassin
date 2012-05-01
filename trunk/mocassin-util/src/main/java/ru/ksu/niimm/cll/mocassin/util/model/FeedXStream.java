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
package ru.ksu.niimm.cll.mocassin.util.model;



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
