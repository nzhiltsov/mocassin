package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.ksu.niimm.cll.mocassin.fulltext.EmptyResultException;
import ru.ksu.niimm.cll.mocassin.fulltext.PDFIndexer;
import ru.ksu.niimm.cll.mocassin.nlp.ParsedDocument;
import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.nlp.Token;
import ru.ksu.niimm.cll.mocassin.nlp.impl.ReferenceImpl;
import ru.ksu.niimm.cll.mocassin.nlp.impl.StructuralElementImpl;
import ru.ksu.niimm.cll.mocassin.nlp.recognizer.StructuralElementTypeRecognizer;
import ru.ksu.niimm.cll.mocassin.nlp.util.StopWordLoader;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.EdgeType;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.EnclosingNodePredicate;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodePositionComparator;
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodePositionPredicate;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardEnvironments;
import ru.ksu.niimm.cll.mocassin.util.CollectionUtil;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class LatexStructuralElementSearcherImpl implements
		LatexStructuralElementSearcher {
	private static final String EQUATION_ENVIRONMENT_NAME = "equation";
	private static final int DOCUMENT_MAX_SIZE = 50 * 1024 * 1024;
	@Inject
	private Logger logger;
	@Inject
	private StructureBuilder structureBuilder;
	@Inject
	private StructuralElementTypeRecognizer structuralElementTypeRecognizer;
	@Inject
	private PDFIndexer pdfIndexer;
	@Inject
	private StopWordLoader stopWordLoader;

	private List<Edge<Node, Node>> edges;

	private ParsedDocument parsedDocument;

	@Override
	public void parse(InputStream inputStream, ParsedDocument parsedDocument,
			boolean closeStream) throws LatexSearcherParseException {
		this.parsedDocument = parsedDocument;
		try {
			InputStream parsingInputStream;
			if (!inputStream.markSupported()) {
				parsingInputStream = new BufferedInputStream(inputStream);
			} else {
				parsingInputStream = inputStream;
			}
			parsingInputStream.mark(DOCUMENT_MAX_SIZE);
			this.edges = this.structureBuilder.buildStructureGraph(
					parsingInputStream, false);
			parsingInputStream.reset();
			extractTextContents(parsingInputStream);

			if (closeStream) {
				parsingInputStream.close();
			}
		} catch (Exception e) {
			throw new LatexSearcherParseException(e.getCause());
		}
	}

	/**
	 * TODO: traversing a list is non-efficient; refactoring from a list of
	 * edges to a list of nodes is required!!
	 * 
	 * @param parsingInputStream
	 * @throws IOException
	 */
	private void extractTextContents(InputStream parsingInputStream)
			throws IOException {

		List<Node> indexingNodes = new ArrayList<Node>();
		Set<Node> nodes = new HashSet<Node>();
		for (Edge<Node, Node> edge : this.edges) {
			Node from = edge.getFrom();

			String fromName = from.getName();
			if (from.isEnvironment()
					&& !fromName.equals(EQUATION_ENVIRONMENT_NAME)
					&& !StandardEnvironments.contains(fromName)) {
				indexingNodes.add(from);
			}
			Node to = edge.getTo();
			String toName = to.getName();
			if (to.isEnvironment() && !toName.equals(EQUATION_ENVIRONMENT_NAME)
					&& !StandardEnvironments.contains(toName)) {
				indexingNodes.add(to);
			}

			nodes.add(from);
			nodes.add(to);
		}
		Collections.sort(indexingNodes, new NodePositionComparator());

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				parsingInputStream));

		int currentLineNumber = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			currentLineNumber++;
			/**
			 * TODO: skip the border of a segment in a more accurate way
			 */
			if (Iterables.find(nodes, new NodePositionPredicate(
					currentLineNumber), null) != null) {
				continue;
			}
			Iterable<Node> enclosingNodes = Iterables.filter(indexingNodes,
					new EnclosingNodePredicate(currentLineNumber));
			List<String> tokens = StringUtil.stripLatexMarkup(line);
			Iterable<String> tokensForIndex = Iterables.filter(tokens,
					getNonStopWordPredicate());
			String[] contents = Iterables.toArray(tokensForIndex, String.class);
			for (Node enclosingNode : enclosingNodes) {
				enclosingNode.addContents(contents);
			}

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
		Set<Node> nodes = new HashSet<Node>();
		for (Edge<Node, Node> edge : this.edges) {
			Node from = edge.getFrom();

			nodes.add(from);
			Node to = edge.getTo();
			nodes.add(to);
		}
		return CollectionUtil.asList(Iterables.transform(nodes,
				new Node2ElementFunction()));
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

	private String getPdfUri() {
		return parsedDocument.getPdfUri();
	}

	private int getPageNumber(String fullTextQuery) throws EmptyResultException {
		return pdfIndexer.getPageNumber(getPdfUri(), fullTextQuery);
	}

	public Predicate<String> getNonStopWordPredicate() {
		return stopWordLoader.getNonStopWordPredicate();
	}

	private class Node2ElementFunction implements
			Function<Node, StructuralElement> {

		private static final int MINIMAL_TOKEN_COUNT = 6;

		@Override
		public StructuralElement apply(Node node) {
			String uri = String.format("%s/s%s", parsedDocument.getFilename(),
					node.getId());
			StructuralElement element = new StructuralElementImpl.Builder(
					uri.hashCode()).uri(uri).name(node.getName()).build();
			List<String> labels = new ArrayList<String>();
			labels.add(node.getLabelText());
			element.setLabels(labels);
			String[] contents = node.getContents().toArray(
					new String[node.getContents().size()]);
			element.setContents(contents);

			if (element.getContents().size() >= MINIMAL_TOKEN_COUNT) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < MINIMAL_TOKEN_COUNT; i++) {
					sb.append(String
							.format("%s ", element.getContents().get(i)));
				}
				try {
					int pageNumber = getPageNumber(sb.toString());
					element.setStartPageNumber(pageNumber);
				} catch (EmptyResultException e) {
					logger.log(
							Level.SEVERE,
							String.format(
									"failed to find the page number for a segment %s on PDF: %s",
									element.getUri(), getPdfUri()));
				}
			}

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

	/**
	 * Stub implementation; genuine analysis is required
	 * 
	 * @author linglab
	 * 
	 */
	private class Edge2ReferenceFunction implements
			Function<Edge<Node, Node>, Reference> {
		private int count;

		private ParsedDocument document;

		private final Node2ElementFunction node2ElementFunction = new Node2ElementFunction();

		public Edge2ReferenceFunction(ParsedDocument document) {
			this.document = document;
		}

		@Override
		public Reference apply(Edge<Node, Node> edge) {
			StructuralElement from = node2ElementFunction.apply(edge.getFrom());
			StructuralElement to = node2ElementFunction.apply(edge.getTo());
			Reference ref = new ReferenceImpl.Builder(++count)
					.document(document).from(from).to(to).build();
			if (edge.getContext().getEdgeType() == EdgeType.CONTAINS) {
				ref.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
			} else if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
				ref.setPredictedRelation(MocassinOntologyRelations.REFERS_TO);
			}

			return ref;
		}
	}
}
