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

import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;

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
