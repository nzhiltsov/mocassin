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
package ru.ksu.niimm.cll.mocassin.frontend.server;

import ru.ksu.niimm.cll.mocassin.frontend.client.OntologyService;
import ru.ksu.niimm.cll.mocassin.frontend.client.QueryService;
import ru.ksu.niimm.cll.mocassin.frontend.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.frontend.dashboard.server.ArXMLivAdapterService;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.ViewerService;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.server.ViewerServiceImpl;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.server.util.OntologyElementConverter;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.server.util.OntologyElementConverterImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;

public class MocassinUIModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("*/GWT.rpc").with(GuiceRemoteServiceServlet.class);
		serve("/mocassin/download/arxivid/*").with(PdfDownloadServlet.class);
		serve("*/describe").with(DescriptionServlet.class);
		bind(OntologyService.class).to(OntologyServiceImpl.class);
		bind(QueryService.class).to(QueryServiceImpl.class);
		bind(ArxivService.class).to(ArXMLivAdapterService.class);
		bind(ViewerService.class).to(ViewerServiceImpl.class);
		bind(OntologyElementConverter.class).to(
				OntologyElementConverterImpl.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
