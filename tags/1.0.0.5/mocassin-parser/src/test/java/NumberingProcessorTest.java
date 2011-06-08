import java.util.SortedSet;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.cll.mocassin.parser.LatexParserModule;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodePositionComparator;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl.NumberingProcessor;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext( { LatexParserModule.class })
public class NumberingProcessorTest {
	@Inject
	private NumberingProcessor numberingProcessor;

	@Test
	public void testProcessConsecutiveNumbers() {
		Node node1 = new NodeImpl.Builder("1", "section").title("Introduction")
				.beginLine(220).endLine(275).offset(0).numbered(true).build();
		Node node2 = new NodeImpl.Builder("2", "theorem").title("Theorem")
				.beginLine(238).endLine(241).offset(0).numbered(true).build();
		Node node3 = new NodeImpl.Builder("3", "section").title("Background")
				.beginLine(276).endLine(374).offset(0).numbered(true).build();
		Node node4 = new NodeImpl.Builder("4", "definition")
				.title("Definition").beginLine(314).endLine(322).offset(0)
				.numbered(true).build();
		Node node5 = new NodeImpl.Builder("5", "section").title(
				"Convergence Results").beginLine(375).endLine(544).offset(0)
				.numbered(true).build();
		Node node6 = new NodeImpl.Builder("6", "proposition").title(
				"Proposition").beginLine(394).endLine(397).offset(0).numbered(
				true).build();
		Node node7 = new NodeImpl.Builder("7", "proposition").title(
				"Proposition").beginLine(475).endLine(477).offset(0).numbered(
				true).build();
		SortedSet<Node> nodes = Sets.newTreeSet(new NodePositionComparator());
		nodes.add(node1);
		nodes.add(node2);
		nodes.add(node3);
		nodes.add(node4);
		nodes.add(node5);
		nodes.add(node6);
		nodes.add(node7);
		this.numberingProcessor.processConsecutiveNumbers(nodes);
		Node[] nodesArray = Iterables.toArray(nodes, Node.class);
		Assert.assertEquals("1 Introduction", nodesArray[0].getTitle());
		Assert.assertEquals("Theorem 1", nodesArray[1].getTitle());
		Assert.assertEquals("2 Background", nodesArray[2].getTitle());
		Assert.assertEquals("Definition 1", nodesArray[3].getTitle());
		Assert.assertEquals("3 Convergence Results", nodesArray[4].getTitle());
		Assert.assertEquals("Proposition 1", nodesArray[5].getTitle());
		Assert.assertEquals("Proposition 2", nodesArray[6].getTitle());
	}
}
