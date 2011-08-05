package ru.ksu.niimm.cll.mocassin.nlp.gate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.csvreader.CsvReader;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({ GateModule.class})
public class GateProcessingFacadeTest {
	@Inject
	private GateProcessingFacade gateProcessingFacade;

	private final List<String> ids = new LinkedList<String>();

	@Before
	public void init() throws IOException {
		CsvReader reader = new CsvReader(new InputStreamReader(this.getClass()
				.getClassLoader().getResourceAsStream("mathnet_selected.csv")));
		reader.setTrimWhitespace(true);
		try {
			while (reader.readRecord()) {
				String id = reader.get(0);
				ids.add(id);
			}
		} finally {
			reader.close();
		}
	}

	@Test
	public void testProcess() throws AccessGateDocumentException,
			AccessGateStorageException, ProcessException {
		gateProcessingFacade.process("math/0002188");
		gateProcessingFacade.process("math/0001036");
	}

	@Test @Ignore
	public void testProcessMathnetArticles() {
		for (String mathnetKey : ids) {
			try {
				gateProcessingFacade.process(mathnetKey);
			} catch (Exception e) { // nothing
			}
		}
	}
}
