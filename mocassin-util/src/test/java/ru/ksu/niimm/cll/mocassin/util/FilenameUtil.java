/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.csvreader.CsvReader;

public class FilenameUtil {

	private static final String PROCESSED_SAARB_DIR = "/opt/mocassin/tex/final/";
	private static final String SAARB_COLLECTION_DIR = "/opt/mocassin/tex/";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		CsvReader reader = new CsvReader(new FileReader(
				"/tmp/filename_id_list.csv"), ',');
		reader.setTrimWhitespace(true);
		reader.readHeaders();
		while (reader.readRecord()) {
			String filename = reader.get("filename");
			String arxivid = reader.get("arxivid");
			String clearId = extractId(arxivid);
			String newFilename = String.format("%s.tex", clearId).replace(
					"/", "_");

			try {
				FileUtils.copyFile(new File(SAARB_COLLECTION_DIR + filename),
						new File(PROCESSED_SAARB_DIR + newFilename));
			} catch (Exception e) {
				System.out.println(String.format(
						"failed to rename the file '%s'", filename));
			}

		}
		reader.close();
	}

	private static String extractId(String arxivid) {
		return arxivid.replace("http://arxiv.org/abs/", "").replaceFirst(
				"v[0-9]+", "");
	}
}
