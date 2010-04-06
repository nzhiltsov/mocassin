package unittest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFNode.Type;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFNodeImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public class VirtuosoDAOTest {
	private static final String MOCASSIN_TEST_URL = "jdbc:virtuoso://192.168.2.143:1111";
	private static final String MOCASSIN_TEST_PASSWORD = "mocassintest";
	private static final String MOCASSIN_TEST_USERNAME = "mocassinuser";
	private static final String MOCASSIN_TEST_GRAPH_IRI = "<http://cll.niimm.ksu.ru/mocassintest>";
	@Inject
	private VirtuosoDAO virtuosoDAO;

	@Test
	public void testInsert() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		RDFNode subject = new RDFNodeImpl(
				"<http://cll.niimm.ksu.ru/all.omdoc#Goedels-incompleteness-pfsketch.p7>",
				Type.IRI_REFERENCE);
		RDFNode predicate = new RDFNodeImpl(
				"<http://omdoc.org/ontology#formalityDegree>",
				Type.IRI_REFERENCE);
		RDFNode object = new RDFNodeImpl("<http://omdoc.org/ontology#Formal>",
				Type.IRI_REFERENCE);
		RDFTriple triple = new RDFTripleImpl(subject, predicate, object);
		triples.add(triple);
		RDFGraph graph = new RDFGraphImpl.Builder(MOCASSIN_TEST_GRAPH_IRI)
				.username(MOCASSIN_TEST_USERNAME).password(
						MOCASSIN_TEST_PASSWORD).url(MOCASSIN_TEST_URL).build();

		getVirtuosoDAO().insert(triples, graph);
	}

	public VirtuosoDAO getVirtuosoDAO() {
		return virtuosoDAO;
	}

}
