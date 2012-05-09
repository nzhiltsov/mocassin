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

import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 
 * Document part
 * 
 * @author nzhiltsov
 * 
 */
public interface Node {

    String getId();

    String getName();

    boolean equals(Object o);

    int hashCode();

    String getLabelText();

    void setLabelText(String labelText);

    List<String> getContents();

    void addContents(String... text);

    int getBeginLine();

    int getEndLine();

    int getOffset();

    boolean isEnvironment();

    String getTitle();

    void setTitle(String title);

    boolean isNumbered();

    /**
     * returns the start page number in the generated PDF file, on which a node
     * is located
     * 
     * @return
     */
    int getPdfPageNumber();

    void setPdfPageNumber(int pageNumber);
}
