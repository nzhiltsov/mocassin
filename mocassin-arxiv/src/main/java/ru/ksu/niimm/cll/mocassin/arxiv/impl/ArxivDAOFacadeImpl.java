package ru.ksu.niimm.cll.mocassin.arxiv.impl;

import java.io.InputStream;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.util.ArticleMetadataReader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ArxivDAOFacadeImpl implements ArxivDAOFacade {
	private static final String DEFAULT_CHARSET = "UTF-8";

	private String arxivConnectionUrl;

	private boolean isUseProxy;

	private String proxyHost;

	private int proxyPort;

	private String proxyUser;

	private String proxyPassword;

	@Inject
	private Logger logger;

	@Inject
	public ArxivDAOFacadeImpl(
			@Named("arxiv.api.connection.url") String arxivConnectionUrl,
			@Named("isUseProxy") boolean isUseProxy,
			@Named("proxy.host") String proxyHost,
			@Named("proxy.port") int proxyPort,
			@Named("proxy.user") String proxyUser,
			@Named("proxy.password") String proxyPassword) {
		this.arxivConnectionUrl = arxivConnectionUrl;
		this.isUseProxy = isUseProxy;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPassword = proxyPassword;
	}

	@Override
	public ArticleMetadata retrieve(String arxivId) {
		// TODO: more subtle validation is required
		if (arxivId == null)
			throw new IllegalArgumentException("arxiv id cannot be null");
		try {
			String paramValue = arxivId;
			String query = String.format("id_list=%s", URLEncoder.encode(
					paramValue, DEFAULT_CHARSET));
			URL url = new URL(this.arxivConnectionUrl + "?" + query);
			Proxy proxy = null;
			if (isUseProxy) {
				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(proxyUser,
								proxyPassword.toCharArray());
					}
				});
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxyHost, proxyPort));

			} else {
				proxy = Proxy.NO_PROXY;
			}
			URLConnection connection = url.openConnection(proxy);

			connection.setRequestProperty("Accept-Charset", DEFAULT_CHARSET);
			connection.setRequestProperty("Content-Type", "application/xml");

			InputStream response = connection.getInputStream();

			ArticleMetadata metadata = ArticleMetadataReader.read(response);

			if (isUseProxy) {
				Authenticator.setDefault(null);
			}
			return metadata;

		} catch (Exception e) {
			logger.log(Level.SEVERE,
					"failed to process the request with arXiv id:" + arxivId
							+ " due to:" + e.getMessage());
			return null;
		}
	}
}
