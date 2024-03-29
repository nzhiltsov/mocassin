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

import gate.Document;

import java.util.LinkedList;
import java.util.List;

public interface CitationSearcher {
    List<String> getCitationSentences(Document document, String paperUrl);

    /**
     * returns an ordered list of citations for the document with a given id
     * 
     * @return
     */
    LinkedList<Citation> getCitations(Document document, String paperUrl);
}
