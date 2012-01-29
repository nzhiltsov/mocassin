package ru.ksu.niimm.cll.mocassin.parser.pdf;

import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.latex.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.util.AbstractUnixCommandWrapper;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ LatexParserModule.class, PdfParserModule.class })
public class WhoAmITest {
	@Inject
	WhoAmIChecker whoAmIChecker;

	@Test
	public void testExecute() throws Exception {
		this.whoAmIChecker.execute();
	}

	private static class WhoAmIChecker extends AbstractUnixCommandWrapper {

		@Inject
		public WhoAmIChecker(Logger logger) {
			super(logger, 1);
			this.cmdArray[0] = "whoami";
		}

	}
}
