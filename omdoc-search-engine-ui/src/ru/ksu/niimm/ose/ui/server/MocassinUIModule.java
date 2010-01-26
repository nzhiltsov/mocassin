package ru.ksu.niimm.ose.ui.server;

import ru.ksu.niimm.ose.ui.client.OntologyService;

import com.google.inject.servlet.ServletModule;

public class MocassinUIModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/mocassin/GWT.rpc").with(GuiceRemoteServiceServlet.class);
		serve("/mocassin/download").with(DownloadServlet.class);
		bind(OntologyService.class).to(OntologyServiceImpl.class);
	}
}
