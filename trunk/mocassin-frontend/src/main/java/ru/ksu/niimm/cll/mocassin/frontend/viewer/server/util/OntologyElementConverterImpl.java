/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.frontend.viewer.server.util;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.Graph;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.Node;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis.LinkAdapter;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.SGEdge;

public class OntologyElementConverterImpl implements OntologyElementConverter {

	@Override
	public Graph convert(List<SGEdge> edges) {
		List<Node> nodes = new ArrayList<Node>();
		LinkAdapter[] links = new LinkAdapter[edges.size()];
		int i = 0;
		for (SGEdge edge : edges) {
			OntologyIndividual subject = edge.getSubject();
			String subjectTitle = edge.getFromTitle() != null ? edge
					.getFromTitle() : subject.getType().toString();
			Node subjectNode = new Node(subject.getUri(), subjectTitle, subject
					.getType().getCode());
			subjectNode.setNumPage(edge.getFromNumPage());
			if (!nodes.contains(subjectNode)) {
				nodes.add(subjectNode);
			}
			OntologyIndividual object = edge.getObject();
			Node objectNode = new Node(object.getUri(),
					edge.getToTitle() != null ? edge.getToTitle() : object
							.getType().toString(), object.getType().getCode());
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

}
