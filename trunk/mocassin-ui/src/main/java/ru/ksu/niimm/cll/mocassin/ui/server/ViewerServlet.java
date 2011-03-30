package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class ViewerServlet extends HttpServlet {
	private static final String ERROR_URL = "/error.html";
	private static final String STRUCTURE_VIEWER_URL = "/StructureViewer.html";
	private static final String RESOURCE_URI_PARAMETER_NAME = "resourceuri";
	@Inject
	private Logger logger;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String resourceUri = req.getParameter(RESOURCE_URI_PARAMETER_NAME);
		String dispatchedUri;
		if (resourceUri != null) {
			logger.log(Level.INFO, String.format("clicked URL to view: %s",
					resourceUri));
			dispatchedUri = STRUCTURE_VIEWER_URL;
		} else {
			dispatchedUri = ERROR_URL;
		}

		RequestDispatcher dispatcher = req.getRequestDispatcher(dispatchedUri);
		dispatcher.forward(req, resp);
	}

}
