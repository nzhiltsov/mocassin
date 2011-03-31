package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class ViewerFilter implements Filter {
	private static final String ERROR_URL = "/error.html";
	private static final String RESOURCE_URI_PARAMETER_NAME = "resourceuri";
	@Inject
	private Log logger = LogFactory.getLog(ViewerFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		String resourceUri = req.getParameter(RESOURCE_URI_PARAMETER_NAME);
		if (resourceUri != null) {
			logger.info(String.format("clicked URL to view: %s", resourceUri));
		} else {
			((HttpServletResponse) resp).sendRedirect(ERROR_URL);
		}

		chain.doFilter(req, resp);

	}

	@Override
	public void destroy() {

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
