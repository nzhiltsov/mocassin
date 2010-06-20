package ru.ksu.niimm.cll.mocassin.ui.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

import ru.ksu.niimm.cll.mocassin.ui.server.util.FileUtil;

@SuppressWarnings("serial")
@Singleton
public class DownloadServlet extends HttpServlet {
	private static final String FILE_URI_PREFIX = "file:/";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String filePath = req.getParameter("url");
		byte[] buff = FileUtil.readFile(parseFileName(filePath));

		resp.setContentType(getContentType(filePath));
		resp.setHeader("Content-disposition", String.format(
				"attachment; filename=%s", parseShortName(filePath)));
		ServletOutputStream outputStream = resp.getOutputStream();
		outputStream.write(buff);
		outputStream.close();
	}

	private String parseFileName(String string) {
		// TODO : bad implementation, need more accurate parse file name string
		String clearedString = string.startsWith(FILE_URI_PREFIX) ? string
				.replaceFirst(FILE_URI_PREFIX, "") : new String(string);
		return clearedString.startsWith("/") ? clearedString : String.format(
				"/%s", clearedString);

	}

	private String parseShortName(String fileName) {
		return fileName.substring(fileName.lastIndexOf("/") + 1);
	}

	private String parseExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	private String getContentType(String fileName) {
		String extension = parseExtension(fileName);
		return ContentTypes.convertFromExtension(extension).getMime();
	}

	enum ContentTypes {
		Latex("application/latex", "tex"), Pdf("application/pdf", "pdf"), OctetStream(
				"application/octet-stream", "");

		private String mime;
		private String extension;

		ContentTypes(String name, String extension) {
			this.mime = name;
			this.extension = extension;
		}

		public String getMime() {
			return mime;
		}

		public static ContentTypes convertFromExtension(String extension) {
			for (ContentTypes contentType : ContentTypes.values()) {
				if (contentType.extension.equals(extension)) {
					return contentType;
				}

			}
			return OctetStream;
		}
	}
}
