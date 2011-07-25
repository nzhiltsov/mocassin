package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class PdfDownloadServlet extends HttpServlet {
	private static final String ARXIVID_ENTRY = "/arxivid/";
	private static int ARXIVID_ENTRY_LENGTH = ARXIVID_ENTRY.length();
	@Inject
	private Logger logger;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		String requestURI = req.getRequestURI();
		logger.log(Level.INFO, "request URI: " + requestURI);
		String parameter = requestURI.substring(
				requestURI.lastIndexOf(ARXIVID_ENTRY) + ARXIVID_ENTRY_LENGTH,
				requestURI.indexOf("&"));
		int signIndex = parameter
				.indexOf(StringUtil.ARXIVID_SEGMENTID_DELIMITER);
		String arxivId = signIndex != -1 ? parameter.substring(0, signIndex)
				: parameter;
		String segmentId = signIndex != -1 ? parameter.substring(signIndex + 1)
				: null;

		if (arxivId == null) {
			logger.log(Level.SEVERE,
					"obtained a request with an empty arxiv id parameter");
			return;
		}
		String filePath = segmentId == null ? "/opt/mocassin/aux-pdf/"
				+ StringUtil.arxivid2filename(arxivId, "pdf")
				: "/opt/mocassin/pdf/"
						+ StringUtil.segmentid2filename(arxivId,
								Integer.parseInt(segmentId), "pdf");

		try {
			FileInputStream fileInputStream = new FileInputStream(filePath);
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			IOUtils.copy(fileInputStream, byteArrayOutputStream);
			resp.setContentType("application/pdf");
			resp.setHeader(
					"Content-disposition",
					String.format("attachment; filename=%s",
							StringUtil.arxivid2filename(arxivId, "pdf")));
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.close();
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, String.format(
					"error while downloading: PDF file='%s' not found",
					filePath));
		} catch (IOException e) {
			logger.log(
					Level.SEVERE,
					String.format("error while downloading due to: %s",
							e.getMessage()));
		}
	}
}
