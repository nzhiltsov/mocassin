package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node;
import ru.ksu.niimm.ose.ontology.ABoxTriple;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;

import com.google.common.base.Function;

public class OntologyElementConverterImpl implements OntologyElementConverter {

	@Override
	public Graph convert(List<ABoxTriple> triples) {
		List<Node> nodes = new ArrayList<Node>();
		Link[] links = new Link[triples.size()];
		int i = 0;
		for (ABoxTriple triple : triples) {
			OntologyIndividual subject = triple.getSubject();
			Node subjectNode = new NodeFunction().apply(subject);
			if (!nodes.contains(subjectNode)) {
				nodes.add(subjectNode);
			}
			OntologyIndividual object = triple.getObject();
			Node objectNode = new NodeFunction().apply(object);
			if (!nodes.contains(objectNode)) {
				nodes.add(objectNode);
			}

			MocassinOntologyRelations predicate = triple.getPredicate();
			int predicateCode = predicate.getCode();
			Link link = new Link(nodes.indexOf(subjectNode),
					nodes.indexOf(objectNode), predicateCode);
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
					element.getType().toString(), element.getType().getCode(),
					1);// TODO:
			// extract
			// correct
			// a
			// code
			// and
			// page
			// numbers
			return node;
		}

	}

}
