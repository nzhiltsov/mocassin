package ru.ksu.niimm.cll.mocassin.ui.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	private FileUtil() {
	}

	public static byte[] readFile(String filePath) throws IOException {
		File file = new File(filePath);
		InputStream inputStream = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new RuntimeException("file is too large");
		}

		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = inputStream.read(bytes, offset, bytes.length
						- offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		inputStream.close();
		return bytes;
	}
}
