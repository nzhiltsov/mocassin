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
package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

public class GateFormatConstants {
	public static final String TITLE_ANNOTATION_NAME_PROPERTY_KEY = "title.annotation.name";
	public static final String TOKEN_ANNOTATION_NAME_PROPERTY_KEY = "token.annotation.name";
	public static final String SENTENCE_ANNOTATION_NAME_PROPERTY_KEY = "sentence.annotation.name";
	public static final String SPACE_TOKEN_ANNOTATION_NAME_PROPERTY_KEY = "space.token.annotation.name";
	
	public static final String ARXMLIV_MARKUP_NAME_PROPERTY_KEY = "arxmliv.markup.name";
	public static final String ARXMLIV_MATH_ANNOTATION_PROPERTY_KEY = "arxmliv.math.annotation.name";
	public static final String ARXMLIV_DOCUMENT_ANNOTATION_PROPERTY_KEY = "arxmliv.document.annotation.name";
	public static final String ARXMLIV_REF_ANNOTATION_PROPERTY_KEY = "arxmliv.ref.annotation.name";
	public static final String ARXMLIV_CREATOR_PROPERTY_KEY = "arxmliv.creator.annotation.name";

	public static final String DEFAULT_ANNOTATION_SET_NAME = "";
	public static final String TOKEN_FEATURE_NAME = "string";
	public static final String STEM_FEATURE_NAME = "stem";
	public static final String POS_FEATURE_NAME = "category";
	public static final String VERB_POS_TAG_PREFIX = "vb";
	

	private GateFormatConstants() {
	}
}
