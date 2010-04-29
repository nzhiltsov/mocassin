package unittest;

import junit.framework.Assert;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.DeleteQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;

import com.google.inject.Inject;

public class DeleteQueryGeneratorTest extends AbstractTest {
	private static final String DOCUMENT_URI = "all.omdoc";
	@Inject
	private DeleteQueryGenerator deleteQueryGenerator;

	@Test
	public void testGenerate() {
		String graphIri = getProperties().getProperty("graph.iri");
		RDFGraph graph = new RDFGraphImpl.Builder(graphIri).username(
				getProperties().getProperty("connection.user.name")).password(
				getProperties().getProperty("connection.user.password")).url(
				getProperties().getProperty("connection.url")).build();
		String expression = getDeleteQueryGenerator().generate(DOCUMENT_URI,
				graph);
		Assert
				.assertTrue(expression
						.equalsIgnoreCase(String
								.format(
										"DELETE FROM %s {?s ?p ?o} WHERE {?s ?p ?o. FILTER (regex(?s, \"^%s\") || regex(?o, \"^%s\"))}",
										graphIri, DOCUMENT_URI, DOCUMENT_URI)));
	}

	public DeleteQueryGenerator getDeleteQueryGenerator() {
		return deleteQueryGenerator;
	}

}
