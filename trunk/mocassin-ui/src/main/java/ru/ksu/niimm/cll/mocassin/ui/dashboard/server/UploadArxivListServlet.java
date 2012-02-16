package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.util.IOUtil;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class UploadArxivListServlet extends HttpServlet {
	@InjectLogger
	private Logger logger;
	@Inject
	private ArXMLivAdapter arXMLivAdapter;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator it = upload.getItemIterator(req);
			if (it.hasNext()) {
				FileItemStream item = it.next();
				Set<String> ids = IOUtil.readLineSet(item.openStream());
				logger.debug("Found {} identifiers to upload in the request",
						ids.size());
				int numberOfSuccesses = arXMLivAdapter.handle(ids);
				resp.setContentType("text/html");
				resp.getWriter().printf("{ \"numberOfSuccesses\": %d}",
						numberOfSuccesses);
			}
		} catch (FileUploadException e) {
			logger.error(
					"Failed to upload a list of articles ids for processing", e);
		}

	}

}
