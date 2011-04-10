package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.texlipse.texparser.lexer.LexerException;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;
import ru.ksu.niimm.cll.mocassin.parser.latex.TreeParser;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.Builder;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class LatexStructuralElementSearcherImpl implements
		LatexStructuralElementSearcher {
	@Inject
	private Builder structureAnalyzer;
	@Inject
	private TreeParser treeParser;
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;

	private List<Edge<Node, Node>> edges;

	private LatexDocumentModel model;

	@Override
	public void parse(InputStream stream, ParsedDocument parsedDocument)
			throws LatexSearcherParseException {
		try {
			InputStreamReader reader = new InputStreamReader(stream, "utf8");
			this.model = this.treeParser.parseTree(reader);
			this.model.setDocId(parsedDocument.getFilename());
			this.edges = this.structureAnalyzer.analyze(model);
		} catch (Exception e) {
			throw new LatexSearcherParseException(e.getCause());
		}
	}

	@Override
	public List<Reference> retrieveReferences(ParsedDocument document) {
		return CollectionUtil.asList(Iterables.transform(this.edges,
				new Edge2ReferenceFunction(document)));
	}

	@Override
	public StructuralElement findById(ParsedDocument document, int id) {
		// TODO
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public StructuralElement findClosestPredecessor(ParsedDocument document,
			int id, MocassinOntologyClasses... filterPredecessorTypes) {
		// TODO
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public List<StructuralElement> retrieveElements(ParsedDocument document) {
		List<Node> nodes = new ArrayList<Node>();
		for (Edge<Node, Node> edge : this.edges) {
			Node from = edge.getFrom();
			if (!nodes.contains(from)) {
				nodes.add(from);
			}
			Node to = edge.getTo();
			if (!nodes.contains(to)) {
				nodes.add(to);
			}
		}
		return CollectionUtil.asList(Iterables.transform(nodes,
				new Node2ElementFunction()));
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

	private class Node2ElementFunction implements
			Function<Node, StructuralElement> {

		@Override
		public StructuralElement apply(Node node) {
			String uri = String.format("%s/s%s", model.getDocId(), node.getId());
			StructuralElement element = new StructuralElementImpl.Builder(uri
					.hashCode()).uri(uri).name(node.getName()).build();
			List<String> labels = new ArrayList<String>();
			labels.add(node.getLabelText());
			element.setLabels(labels);
			/**
			 * TODO : extract tokens from LaTeX
			 */
			element.setTitleTokens(new ArrayList<Token>());
			MocassinOntologyClasses predictedClass = getStructuralElementTypeRecognizer()
					.predict(element);
			element.setPredictedClass(predictedClass);

			return element;
		}

	}

	private class Edge2ReferenceFunction implements
			Function<Edge<Node, Node>, Reference> {
		private int count;

		private ParsedDocument document;

		private Node2ElementFunction node2ElementFunction = new Node2ElementFunction();

		public Edge2ReferenceFunction(ParsedDocument document) {
			this.document = document;
		}

		@Override
		public Reference apply(Edge<Node, Node> edge) {
			StructuralElement from = node2ElementFunction.apply(edge.getFrom());
			StructuralElement to = node2ElementFunction.apply(edge.getTo());
			if (from.getPredictedClass() == null
					|| to.getPredictedClass() == null)
				return null;
			Reference ref = new ReferenceImpl.Builder(++count).document(
					document).from(from).to(to).build();
			if (edge.getContext().getEdgeType() == EdgeType.CONTAINS) {
				ref.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
			} else if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
				ref.setPredictedRelation(MocassinOntologyRelations.REFERS_TO);
			}

			return ref;
		}
	}
}
