package unittest.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

public class PdfsyncUtil {

	private static final String PDF_MARK = "s";

	private static final String LATEX_MARK = "l";

	private static final File PDFSYNC_DIR = new File("/opt/mocassin/pdfsync/");

	private static final String OUTPUT_PDFSYNC_DIR = "/opt/mocassin/pdfsync/final/";

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		File[] files = PDFSYNC_DIR.listFiles(new PdfsyncExtensionFilter());
		for (File file : files) {
			try {
				generateSummaryFile(file);
			} catch (Exception e) {
				System.out.println(String.format(
						"processing file='%s' has been failed due to: %s",
						file.getName(), e.getMessage()));
			}
		}

	}

	private static void generateSummaryFile(File file) throws Exception {
		SortedSet<Page> pages = new TreeSet<Page>();

		CsvReader reader = new CsvReader(file.getAbsolutePath(), ' ');
		int startLine = -1;
		int currentLine = -1;
		while (reader.readRecord()) {

			String mark = reader.get(0);
			if (mark.equals(LATEX_MARK)) {
				currentLine = Integer.parseInt(reader.get(2));
				if (startLine == -1) {
					startLine = currentLine;
				}
			}
			if (mark.equals(PDF_MARK)) {
				int pageNumber = Integer.parseInt(reader.get(1));
				Page page = new Page(pageNumber, startLine, currentLine);
				pages.add(page);
				startLine = -1;
				currentLine = -1;
			}

		}
		reader.close();
		printPages(file, pages);
	}

	private static void printPages(File file, SortedSet<Page> pages)
			throws IOException {
		CsvWriter writer = new CsvWriter(OUTPUT_PDFSYNC_DIR + file.getName());
		for (Page page : pages) {
			String[] record = { String.valueOf(page.number),
					String.valueOf(page.startLine),
					String.valueOf(page.endLine) };
			writer.writeRecord(record);
		}
		writer.flush();
		writer.close();
	}

	private static class PdfsyncExtensionFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".pdfsync");
		}
	}

	private static class Page implements Comparable<Page> {
		/**
		 * page number in the PDF file
		 */
		int number;
		/**
		 * start line of this page in the Latex file
		 */
		int startLine;
		/**
		 * end line of this page in the Latex file
		 */
		int endLine;

		public Page(int number, int startLine, int endLine) {
			this.number = number;
			this.startLine = startLine;
			this.endLine = endLine;
		}

		@Override
		public int compareTo(Page p) {
			if (number < p.number)
				return -1;
			if (number > p.number)
				return 1;
			return 0;
		}

	}
}
