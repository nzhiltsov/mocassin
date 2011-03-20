package ru.ksu.niimm.cll.mocassin.arxiv.impl;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.util.ArticleMetadataReader;

public class ArxivDAOFacadeImpl implements ArxivDAOFacade {

	@Inject
	private Logger logger;

	@Override
	public ArticleMetadata retrieve(String arxivId) {
		// TODO: more subtle validation is required
		if (arxivId == null)
			throw new IllegalArgumentException("arxiv id cannot be null");
		try {
			String url = "http://export.arxiv.org/api/query";
			String charset = "UTF-8";
			String paramValue = arxivId;
			String query = String.format("id_list=%s", URLEncoder.encode(
					paramValue, charset));
			URLConnection connection = new URL(url + "?" + query)
					.openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/xml");

			InputStream response = connection.getInputStream();

			return ArticleMetadataReader.read(response);

		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"failed to process the request with arXiv id:" + arxivId
							+ " due to:" + e.getMessage());
			return null;
		}
	}
}
