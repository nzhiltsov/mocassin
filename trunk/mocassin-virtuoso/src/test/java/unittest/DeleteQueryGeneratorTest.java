package unittest;

import junit.framework.Assert;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;

import com.google.inject.Inject;

public class DeleteQueryGeneratorTest extends AbstractTest {
	private static final String DOCUMENT_URI = "all.omdoc";
	@Inject
	private DeleteQueryGenerator deleteQueryGenerator;

	@Test
	public void testGenerate() {

		String expression = getDeleteQueryGenerator().generate(DOCUMENT_URI,
				getGraph());
		Assert
				.assertTrue(expression
						.equalsIgnoreCase(String
								.format(
										"DELETE FROM <%s> {?s ?p ?o} WHERE {?s ?p ?o. FILTER (regex(?s, \"^%s\") || regex(?o, \"^%s\"))}",
										getGraph().getIri(), DOCUMENT_URI,
										DOCUMENT_URI)));
	}

	public DeleteQueryGenerator getDeleteQueryGenerator() {
		return deleteQueryGenerator;
	}

}
