package unittest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.virtuoso.RDFGraph;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoDAO;
import ru.ksu.niimm.cll.mocassin.virtuoso.VirtuosoModule;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFGraphImpl;
import ru.ksu.niimm.cll.mocassin.virtuoso.impl.RDFTripleImpl;

import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(VirtuosoModule.class)
public class VirtuosoDAOTest {
	private static final String MOCASSIN_TEST_GRAPH_URI = "http://mocassin-test";
	@Inject
	private VirtuosoDAO virtuosoDAO;

	@Test
	public void testInsert() {
		List<RDFTriple> triples = new ArrayList<RDFTriple>();
		triples.add(new RDFTripleImpl(
				"all.omdoc#Goedels-incompleteness-pfsketch.p7",
				"http://omdoc.org/ontology#formalityDegree",
				"http://omdoc.org/ontology#Formal"));
		RDFGraph graph = new RDFGraphImpl(MOCASSIN_TEST_GRAPH_URI);
		virtuosoDAO.insert(triples, graph);
	}
}
