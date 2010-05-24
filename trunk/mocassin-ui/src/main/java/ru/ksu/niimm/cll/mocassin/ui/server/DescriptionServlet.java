package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.ksu.niimm.ose.ontology.QueryManagerFacade;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hp.hpl.jena.rdf.model.Model;

@SuppressWarnings("serial")
@Singleton
public class DescriptionServlet extends HttpServlet {
	private static final String RESOURCE_URI_PARAMETER_NAME = "resourceuri";
	private static final String CONTENT_TYPE = "application/rdf+xml; charset=UTF-8";
	@Inject
	private QueryManagerFacade queryManagerFacade;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String resourceUri = req.getParameter(RESOURCE_URI_PARAMETER_NAME);
		resp.setContentType(CONTENT_TYPE);
		ServletOutputStream outputStream = resp.getOutputStream();
		Model model = getQueryManagerFacade().describe(resourceUri);
		model.write(outputStream);
		outputStream.close();
	}

	public QueryManagerFacade getQueryManagerFacade() {
		return queryManagerFacade;
	}

}
