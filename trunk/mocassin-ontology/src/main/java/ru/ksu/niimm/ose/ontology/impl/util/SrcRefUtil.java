package ru.ksu.niimm.ose.ontology.impl.util;

import java.util.StringTokenizer;

import ru.ksu.niimm.ose.ontology.SourceReference;

public class SrcRefUtil {
	private SrcRefUtil() {
	}

	public static SourceReference parse(String resourceUri, String srcRefValue) {
		if (srcRefValue == null || srcRefValue.equals("")) {
			// TODO : correct the handling in this case
			SourceReference fakeRef = new SourceReference();
			fakeRef.setFileName("undefined_this_is_fake_document.tex");
			return fakeRef;
		}

		StringTokenizer st = new StringTokenizer(srcRefValue, ";");
		String fileNameStr = st.nextToken();
		String coordinateStr = st.nextToken();

		StringTokenizer fileNameTokenizer = new StringTokenizer(fileNameStr);
		assert fileNameTokenizer.countTokens() == 2;
		String fileName = null;
		while (fileNameTokenizer.hasMoreTokens()) {
			fileName = fileNameTokenizer.nextToken();
		}

		StringTokenizer coordinateTokenizer = new StringTokenizer(coordinateStr);
		assert coordinateTokenizer.countTokens() == 4;
		String[] coordinateTokens = new String[coordinateTokenizer
				.countTokens()];
		int i = 0;
		while (coordinateTokenizer.hasMoreTokens()) {
			coordinateTokens[i] = coordinateTokenizer.nextToken();
			i++;
		}
		int line = Integer.parseInt(coordinateTokens[1]);
		int column = Integer.parseInt(coordinateTokens[3]);

		String folderPath = resourceUri.substring(0, resourceUri
				.lastIndexOf("/"));

		SourceReference srcRef = new SourceReference();
		srcRef.setFileName(String.format("%s/%s", folderPath, fileName));
		srcRef.setLine(line);
		srcRef.setColumn(column);
		return srcRef;
	}
}
