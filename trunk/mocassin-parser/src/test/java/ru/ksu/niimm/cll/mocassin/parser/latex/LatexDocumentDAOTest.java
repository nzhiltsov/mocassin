package ru.ksu.niimm.cll.mocassin.parser.latex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.pdf.PdfParserModule;

import com.csvreader.CsvReader;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class LatexDocumentDAOTest {
	@Inject
	private LatexDocumentDAO latexDocumentDAO;

	@Test
	public void testLoad() {

		LatexDocumentModel doc = latexDocumentDAO.load("ivm18");
		Assert.assertEquals(68, doc.getReferences().size());
	}
}
