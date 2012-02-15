package ru.ksu.niimm.cll.mocassin.ui.server;

import ru.ksu.niimm.cll.mocassin.ui.client.OntologyService;
import ru.ksu.niimm.cll.mocassin.ui.client.QueryService;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.server.ArXMLivAdapter;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.server.ArXMLivAdapterService;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.server.MathnetAdapter;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.server.UploadArxivListServlet;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ViewerService;
import ru.ksu.niimm.cll.mocassin.ui.viewer.server.ViewerServiceImpl;
import ru.ksu.niimm.cll.mocassin.ui.viewer.server.util.OntologyElementConverter;
import ru.ksu.niimm.cll.mocassin.ui.viewer.server.util.OntologyElementConverterImpl;
import ru.ksu.niimm.cll.mocassin.util.inject.log.Slf4jTypeListener;

import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.ServletModule;

public class MocassinUIModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("*/GWT.rpc").with(GuiceRemoteServiceServlet.class);
		serve("/mocassin/download/arxivid/*").with(PdfDownloadServlet.class);
		serve("/mocassin/upload/arxivlist").with(UploadArxivListServlet.class);
		serve("*/describe").with(DescriptionServlet.class);
		bind(OntologyService.class).to(OntologyServiceImpl.class);
		bind(QueryService.class).to(QueryServiceImpl.class);
		bind(ArxivService.class).to(ArXMLivAdapterService.class);
		bind(ViewerService.class).to(ViewerServiceImpl.class);
		bind(OntologyElementConverter.class).to(
				OntologyElementConverterImpl.class);
		bind(ArXMLivAdapter.class).to(MathnetAdapter.class);
		bindListener(Matchers.any(), new Slf4jTypeListener());
	}
}
