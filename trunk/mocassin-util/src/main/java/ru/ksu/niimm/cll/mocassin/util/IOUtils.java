package ru.ksu.niimm.cll.mocassin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

public class IOUtils {
	private IOUtils() {
	}

	/**
	 * read set of string lines from input stream and close it
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static Set<String> readLineSet(InputStream stream)
			throws IOException {
		Set<String> values = new HashSet<String>();
		try {
			LineNumberReader lineReader = new LineNumberReader(
					new InputStreamReader(stream));
			String line = null;
			while ((line = lineReader.readLine()) != null) {
				values.add(line);
			}
			return values;
		} finally {
			stream.close();
		}

	}
}
