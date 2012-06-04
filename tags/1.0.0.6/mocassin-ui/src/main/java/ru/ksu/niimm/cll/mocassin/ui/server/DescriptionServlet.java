package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class DescriptionServlet extends HttpServlet {
	private static final String RESOURCE_URI_PARAMETER_NAME = "resourceuri";
	private static final String CONTENT_TYPE = "application/rdf+xml; charset=UTF-8";
	@InjectLogger
	private Logger logger;
	@Inject
	private QueryManagerFacade queryManagerFacade;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String resourceUri = req.getParameter(RESOURCE_URI_PARAMETER_NAME);
		resp.setContentType(CONTENT_TYPE);
		ServletOutputStream outputStream = resp.getOutputStream();
		String model = getQueryManagerFacade().describe(resourceUri);
		outputStream.print(model);
		outputStream.close();
		logger.debug(
				"Request for a resource='{}' has been processed successfully",
				resourceUri);
	}

	private QueryManagerFacade getQueryManagerFacade() {
		return queryManagerFacade;
	}

}