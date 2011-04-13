package unittest;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Parser;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(LatexParserModule.class)
public class LatexParserTest {

	@Inject
	private Parser parser;

	private InputStream in;

	@Before
	public void init() throws FileNotFoundException {
		this.in = this.getClass().getResourceAsStream("/example.tex");
	}

	@Test
	public void testGetGraph() throws Exception {
		LatexDocumentModel model = getParser().parse(getInputStream());
		Assert.assertTrue(model.getReferences().size() > 0);
	}

	public Parser getParser() {
		return parser;
	}

	public InputStream getInputStream() {
		return in;
	}

}
