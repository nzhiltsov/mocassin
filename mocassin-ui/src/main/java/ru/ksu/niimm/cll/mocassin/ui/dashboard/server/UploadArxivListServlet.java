package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ru.ksu.niimm.cll.mocassin.util.IOUtils;

@SuppressWarnings("serial")
@Singleton
public class UploadArxivListServlet extends HttpServlet {
	@Inject
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
				Set<String> ids = IOUtils.readLineSet(item.openStream());
				int numberOfSuccesses = arXMLivAdapter.handle(ids);
				resp.setContentType("text/html");
				resp.getWriter().printf("{ \"numberOfSuccesses\": %d}", numberOfSuccesses);
			}
		} catch (FileUploadException e) {
			logger.log(Level.SEVERE, "failed to upload an arXiv list due to "
					+ e.getMessage());
		}

	}

}
