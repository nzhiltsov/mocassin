package unittest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import net.sourceforge.texlipse.texparser.lexer.LexerException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.nlp.NlpModule;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ParsedDocumentImpl;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ NlpModule.class, LatexParserModule.class })
public class LatexStructuralElementSearcherTest {
	@Inject
	private LatexStructuralElementSearcher latexStructuralElementSearcher;

	private InputStream in;

	private ParsedDocument parsedDocument;

	@Before
	public void init() throws Exception {
		this.in = this.getClass().getResourceAsStream("/example.tex");
		parsedDocument = new ParsedDocumentImpl("http://somehost.com/doc");
		latexStructuralElementSearcher.parse(this.in, parsedDocument, true);
	}

	@Test
	public void testRetrieveElements() throws UnsupportedEncodingException,
			LexerException, IOException {

		List<StructuralElement> elements = latexStructuralElementSearcher
				.retrieveElements(parsedDocument);
		int i = 0;
		for (StructuralElement element : elements) {
			if (element.getContents() != null) {
				i++;
				System.out.println(element);
			}
		}
		System.out.println(i);
	}

	@Test
	public void testRetrieveReferences() {
		List<Reference> refs = latexStructuralElementSearcher
				.retrieveReferences(parsedDocument);
		for (Reference ref : refs) {
			System.out.println(ref);
		}
	}
}
