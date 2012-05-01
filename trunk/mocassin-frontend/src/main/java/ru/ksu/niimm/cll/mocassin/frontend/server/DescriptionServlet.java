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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class DescriptionServlet extends HttpServlet {
	private static final String RESPONSE_ENCODING = "UTF-8";
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
		resp.setCharacterEncoding(RESPONSE_ENCODING);
		PrintWriter writer = resp.getWriter();
		String model = getQueryManagerFacade().describe(resourceUri);
		try {
			writer.print(model);
		} finally {
			writer.close();
		}

		logger.debug(
				"Request for a resource='{}' has been processed successfully",
				resourceUri);
	}

	private QueryManagerFacade getQueryManagerFacade() {
		return queryManagerFacade;
	}

}
