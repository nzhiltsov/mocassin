package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
				final Set<String> ids = IOUtil.readLineSet(item.openStream());
				logger.debug("Found {} identifiers to upload in the request",
						ids.size());
				ListeningExecutorService service = MoreExecutors
						.listeningDecorator(Executors.newSingleThreadExecutor());
				ListenableFuture<Integer> indexing = service
						.submit(new Callable<Integer>() {
							public Integer call() {
								return arXMLivAdapter.handle(ids);
							}
						});
				Futures.addCallback(indexing, new FutureCallback<Integer>() {
					public void onSuccess(Integer numberOfSuccesses) {
						logger.info(
								"{} document(s) have been processed successfully",
								numberOfSuccesses);
					}

					public void onFailure(Throwable thrown) {
						logger.error("Failed to process uploaded documents",
								thrown);
					}
				});
				resp.setContentType("text/html");
				resp.getWriter()
						.printf("The documents from the uploaded list have been scheduled for processing.");
			}
		} catch (FileUploadException e) {
			logger.error(
					"Failed to upload a list of articles ids for processing", e);
			resp.setContentType("text/html");
			resp.getWriter().printf(
					"Failed to schedule the documents from the uploaded list.");
		}

	}
}
