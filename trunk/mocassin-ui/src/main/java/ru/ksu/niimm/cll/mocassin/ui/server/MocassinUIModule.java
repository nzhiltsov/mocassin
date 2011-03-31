package ru.ksu.niimm.cll.mocassin.ui.server;

import ru.ksu.niimm.cll.mocassin.ui.client.OntologyService;
import ru.ksu.niimm.cll.mocassin.ui.client.QueryService;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.server.ArxivServiceImpl;

import com.google.inject.servlet.ServletModule;

public class MocassinUIModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("*/GWT.rpc").with(GuiceRemoteServiceServlet.class);
//		serve("/mocassin/download").with(DownloadServlet.class);
		serve("*/describe").with(DescriptionServlet.class);
		serve("*/view").with(ViewerServlet.class);
		bind(OntologyService.class).to(OntologyServiceImpl.class);
		bind(QueryService.class).to(QueryServiceImpl.class);
		bind(ArxivService.class).to(ArxivServiceImpl.class);
	}
}