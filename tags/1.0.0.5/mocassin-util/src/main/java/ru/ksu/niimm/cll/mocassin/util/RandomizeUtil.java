package ru.ksu.niimm.cll.mocassin.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizeUtil {

	private RandomizeUtil() {
	}

	public static List<File> getFiles(String dirName, int number) {
		File dir = new File(dirName);
		File[] files = dir.listFiles();
		List<File> selectedFiles = new ArrayList<File>();
		Random random = new Random();
		int i = 0;
		while (i < number) {
			int randomIndex = random.nextInt(files.length);
			File randomFile = files[randomIndex];
			if (!selectedFiles.contains(randomFile)) {
				selectedFiles.add(randomFile);
				i++;
			}
		}
		return selectedFiles;
	}

}
