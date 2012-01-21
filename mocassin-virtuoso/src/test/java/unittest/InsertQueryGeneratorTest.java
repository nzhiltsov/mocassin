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
	private static final String TRIPLE_2 = "<http://mathnet.ru/ivm18> a <http://www.aktors.org/ontology/portal#Article-Reference> .";
	private static final String TRIPLE_1 = "<http://mathnet.ru/ivm18> <http://www.aktors.org/ontology/portal#has-author> <http://mathnet.ru/FakeAuthor>.";

	@Inject
	private InsertQueryGenerator insertQueryGenerator;

	@Test
	public void testGenerate() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFTriple triple = new RDFTripleImpl(TRIPLE_1);
		RDFTriple triple2 = new RDFTripleImpl(TRIPLE_2);
		triples.add(triple);
		triples.add(triple2);
		String generatedExpression = getInsertQueryGenerator().generate(triples,
				getGraph());
		String initialExpression = String.format(
				"INSERT INTO GRAPH <%s> {%s\n%s\n}",
				getProperties().getProperty("graph.iri"), TRIPLE_1, TRIPLE_2);
		Assert.assertTrue(generatedExpression.equalsIgnoreCase(initialExpression));
	}

	public InsertQueryGenerator getInsertQueryGenerator() {
		return insertQueryGenerator;
	}

}
