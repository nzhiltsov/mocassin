package ru.ksu.niimm.cll.mocassin.ui.viewer.server.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableBiMap;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyTriple;

public class OntologyElementConverterImpl implements OntologyElementConverter {

	@Override
	public Graph convert(List<OntologyTriple> triples) {
		List<Node> nodes = new ArrayList<Node>();
		Link[] links = new Link[triples.size()];
		int i = 0;
		for (OntologyTriple triple : triples) {
			OntologyConcept subject = triple.getSubject();
			Node subjectNode = new NodeFunction().apply(subject);
			if (!nodes.contains(subjectNode)) {
				nodes.add(subjectNode);
			}
			OntologyElement object = triple.getObject();
			Node objectNode = new NodeFunction().apply(object);
			if (!nodes.contains(objectNode)) {
				nodes.add(objectNode);
			}

			OntologyElement predicate = triple.getPredicate();
			int predicateCode = MocassinOntologyRelations.fromUri(
					predicate.getUri()).getCode();
			Link link = new Link(nodes.indexOf(subjectNode),
					nodes.indexOf(objectNode), predicateCode);
			links[i] = link;
			i++;
		}
		
		Node[] nodeArray = (Node[]) nodes.toArray(new Node[nodes.size()]);
		return new Graph(nodeArray, links);
	}

	private static class NodeFunction implements
			Function<OntologyElement, Node> {

		@Override
		public Node apply(OntologyElement element) {
			/*
			 * int code = MocassinOntologyClasses.fromUri(concept.getUri())
			 * .getCode();
			 */
			Node node = new Node(element.getUri(), element.getUri(), 0, 1);// TODO:
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
