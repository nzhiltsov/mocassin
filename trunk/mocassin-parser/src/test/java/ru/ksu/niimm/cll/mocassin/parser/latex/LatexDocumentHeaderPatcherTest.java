package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class })
public class LatexDocumentHeaderPatcherTest {
	private static final Pattern PDFSYNC_PATTERN = Pattern
			.compile("\\\\usepackage\\{pdfsync\\}");
	private static final Pattern XCOLOR_PATTERN = Pattern
			.compile("\\\\usepackage\\{xcolor\\}");
	private static final Pattern FRAMED_PATTERN = Pattern
			.compile("\\\\usepackage\\{framed\\}");
	private static final Pattern COLORLET_PATTERN = Pattern
			.compile("\\\\colorlet\\{shadecolor\\}\\{yellow!30\\}");
	@Inject
	private LatexDocumentHeaderPatcher latexDocumentHeaderPatcher;

	@Test
	public void testPatch() throws FileNotFoundException {
		String arxivId = "math/0107167";
		this.latexDocumentHeaderPatcher.patch(arxivId);
		checkIfFound(arxivId);
		arxivId = "math/0002188";
		this.latexDocumentHeaderPatcher.patch(arxivId);
		checkIfFound(arxivId);
	}

	private void checkIfFound(final String arxivId)
			throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("/opt/mocassin/patched-tex/"
				+ StringUtil.arxivid2filename(arxivId, "tex")));
		boolean found = false;
		boolean foundPdfsync = false;
		boolean foundXcolor = false;
		boolean foundFramed = false;
		boolean foundColorlet = false;
		while (scanner.hasNext() && !found) {
			String line = scanner.next();
			if (!foundPdfsync) {
				foundPdfsync = PDFSYNC_PATTERN.matcher(line).find();
			}
			if (!foundXcolor) {
				foundXcolor = XCOLOR_PATTERN.matcher(line).find();
			}
			if (!foundFramed) {
				foundFramed = FRAMED_PATTERN.matcher(line).find();
			}
			if (!foundColorlet) {
				foundColorlet = COLORLET_PATTERN.matcher(line).find();
			}
			found = foundPdfsync && foundXcolor && foundFramed && foundColorlet;
		}
		scanner.close();
		Assert.assertTrue(found);
	}
}
