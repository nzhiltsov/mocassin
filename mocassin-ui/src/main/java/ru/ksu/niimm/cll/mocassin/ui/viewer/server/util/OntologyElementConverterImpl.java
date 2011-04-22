package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.LinkAdapter;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.SGEdge;

import com.google.common.base.Function;

public class OntologyElementConverterImpl implements OntologyElementConverter {

	@Override
	public Graph convert(List<SGEdge> edges) {
		List<Node> nodes = new ArrayList<Node>();
		LinkAdapter[] links = new LinkAdapter[edges.size()];
		int i = 0;
		for (SGEdge edge : edges) {
			OntologyIndividual subject = edge.getSubject();
			Node subjectNode = new NodeFunction().apply(subject);
			subjectNode.setNumPage(edge.getFromNumPage());
			if (!nodes.contains(subjectNode)) {
				nodes.add(subjectNode);
			}
			OntologyIndividual object = edge.getObject();
			Node objectNode = new NodeFunction().apply(object);
			objectNode.setNumPage(edge.getToNumPage());
			if (!nodes.contains(objectNode)) {
				nodes.add(objectNode);
			}

			MocassinOntologyRelations predicate = edge.getPredicate();
			int predicateCode = predicate.getCode();
			LinkAdapter link = new LinkAdapter(nodes.indexOf(subjectNode),
					nodes.indexOf(objectNode), 1, predicateCode);
			links[i] = link;
			i++;
		}

		Node[] nodeArray = (Node[]) nodes.toArray(new Node[nodes.size()]);
		return new Graph(nodeArray, links);
	}

	private static class NodeFunction implements
			Function<OntologyIndividual, Node> {

		@Override
		public Node apply(OntologyIndividual element) {
			/*
			 * int code = MocassinOntologyClasses.fromUri(concept.getUri())
			 * .getCode();
			 */
			Node node = new Node(element.getUri(),
					element.getType().toString(), element.getType().getCode());
			return node;
		}

	}

}
