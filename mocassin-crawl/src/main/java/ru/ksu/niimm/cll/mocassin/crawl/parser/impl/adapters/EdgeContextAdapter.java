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
package ru.ksu.niimm.cll.mocassin.crawl.parser.impl.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import ru.ksu.niimm.cll.mocassin.crawl.parser.impl.EdgeContextImpl;
import ru.ksu.niimm.cll.mocassin.crawl.parser.latex.EdgeContext;

public class EdgeContextAdapter extends
		XmlAdapter<EdgeContextImpl, EdgeContext> {

	@Override
	public EdgeContextImpl marshal(EdgeContext v) throws Exception {
		return (EdgeContextImpl) v;
	}

	@Override
	public EdgeContext unmarshal(EdgeContextImpl v) throws Exception {
		return v;
	}

}
