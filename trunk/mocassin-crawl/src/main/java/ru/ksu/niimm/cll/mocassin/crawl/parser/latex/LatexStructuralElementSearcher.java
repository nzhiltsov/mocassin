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
package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import java.io.InputStream;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

/**
 * Searcher that enriches representation (by extracting the text contents etc.)
 * of the underlying LaTeX document structure for searching purposes
 * 
 * @author nzhiltsov
 * 
 */
public interface LatexStructuralElementSearcher {
	Graph<StructuralElement, Reference> retrieveGraph(
			InputStream inputStream, ParsedDocument parsedDocument,
			boolean closeStream) throws LatexSearcherParseException;
}
