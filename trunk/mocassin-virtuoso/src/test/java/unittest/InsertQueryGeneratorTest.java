package unittest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.generator.InsertQueryGenerator;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;

public class InsertQueryGeneratorTest extends AbstractTest {
	private static final String TRIPLE_2 = "<all.omdoc#whatislogic> <http://salt.semanticauthoring.org/onto/abstract-document-ontology#hasPart> <all.omdoc#whatislogic.p11> .";
	private static final String TRIPLE_1 = "<all.omdoc#whatislogic> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://omdoc.org/ontology#Theory> .";

	@Inject
	private InsertQueryGenerator insertQueryGenerator;

	@Test
	public void testGenerate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(TRIPLE_1);
		RDFTriple triple2 = new RDFTripleImpl(TRIPLE_2);
		triples.add(triple);
		triples.add(triple2);
		String expression = getInsertQueryGenerator().generate(triples,
				getGraph());
		Assert.assertTrue(expression.equalsIgnoreCase(String.format(
				"INSERT INTO GRAPH %s {%s %s }", getProperties().getProperty(
						"graph.iri"), TRIPLE_1, TRIPLE_2)));
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

}
