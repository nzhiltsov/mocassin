package ru.ksu.niimm.cll.mocassin.nlp.latex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import ru.ksu.niimm.cll.mocassin.parser.impl.NodeImpl.NodeBoundaryPredicate;
import ru.ksu.niimm.cll.mocassin.parser.latex.builder.StructureBuilder;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardMathEnvironments;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardStyleEnvironments;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

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

	private Graph<Node, Edge> latexNodeGraph;

	private Graph<StructuralElement, Reference> structureGraph;

	private ParsedDocument parsedDocument;

	private int count;

	private final Node2ElementFunction node2ElementFunction = new Node2ElementFunction();

	@Override
	public synchronized Graph<StructuralElement, Reference> retrieveGraph(
			InputStream inputStream, ParsedDocument parsedDocument,
			boolean shouldCloseStream) throws LatexSearcherParseException {
		this.count = 0;
		this.parsedDocument = parsedDocument;
		this.structureGraph = new DirectedSparseMultigraph<StructuralElement, Reference>();
		parse(inputStream, shouldCloseStream);
		fillStructureGraph();
		return this.structureGraph;
	}

	private void parse(InputStream inputStream, boolean closeStream)
			throws LatexSearcherParseException {

		try {
			InputStream parsingInputStream;
			if (!inputStream.markSupported()) {
				parsingInputStream = new BufferedInputStream(inputStream);
			} else {
				parsingInputStream = inputStream;
			}
			parsingInputStream.mark(DOCUMENT_MAX_SIZE);
			this.latexNodeGraph = this.structureBuilder.buildStructureGraph(
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

		SortedSet<Node> indexingNodes = new TreeSet<Node>(
				new NodePositionComparator());
		Collection<Node> nodes = this.latexNodeGraph.getVertices();
		for (Node node : nodes) {

			String nodeName = node.getName();
			if (!StandardStyleEnvironments.contains(nodeName)) {
				indexingNodes.add(node);
			}

		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				parsingInputStream));

		int currentLineNumber = 0;
		String line;
		while ((line = reader.readLine()) != null) {
			currentLineNumber++;
			/**
			 * TODO: skip the boundaries of a segment in a more accurate way
			 */
			if (Iterables.find(nodes, new NodeBoundaryPredicate(
					currentLineNumber), null) != null) {
				continue;
			}
			Iterable<Node> enclosingNodes = Iterables.filter(indexingNodes,
					new EnclosingNodePredicate(currentLineNumber));
			if (Iterables.isEmpty(enclosingNodes))
				continue;
			boolean isContainedByMathEnvironment = false;
			for (Node node : enclosingNodes) {
				if (StandardMathEnvironments.contains(node.getName())) {
					isContainedByMathEnvironment = true;
					break;
				}
			}
			if (isContainedByMathEnvironment)
				continue;

			List<String> tokens = StringUtil.stripLatexMarkup(line);
			Iterable<String> tokensForIndex = Iterables.filter(tokens,
					getNonStopWordPredicate());
			String[] contents = Iterables.toArray(tokensForIndex, String.class);
			for (Node enclosingNode : enclosingNodes) {
				if (enclosingNode.getBeginLine() == 612) {
					enclosingNode.getBeginLine();
				}
				enclosingNode.addContents(contents);
			}
		}
	}

	private void fillStructureGraph() {
		Collection<Edge> edges = latexNodeGraph.getEdges();
		for (Edge edge : edges) {
			StructuralElement from = node2ElementFunction.apply(latexNodeGraph
					.getSource(edge));
			StructuralElement to = node2ElementFunction.apply(latexNodeGraph
					.getDest(edge));
			Reference ref = new ReferenceImpl.Builder(++count).document(
					parsedDocument).build();
			if (edge.getContext().getEdgeType() == EdgeType.CONTAINS) {
				ref.setPredictedRelation(MocassinOntologyRelations.HAS_PART);
			} else if (edge.getContext().getEdgeType() == EdgeType.REFERS_TO) {
				ref.setPredictedRelation(MocassinOntologyRelations.REFERS_TO);
			}
			addEdge(ref, from, to);
		}

	}

	private void addEdge(Reference edge, final StructuralElement from,
			final StructuralElement to) {
		StructuralElement foundFrom = null;
		StructuralElement foundTo = null;
		if (this.structureGraph.containsVertex(from)) {
			foundFrom = findVertice(from);
		}
		if (this.structureGraph.containsVertex(to)) {
			foundTo = findVertice(to);
		}
		this.structureGraph.addEdge(edge, foundFrom != null ? foundFrom : from,
				foundTo != null ? foundTo : to);
	}

	private StructuralElement findVertice(StructuralElement node) {
		Collection<StructuralElement> vertices = this.structureGraph
				.getVertices();
		for (StructuralElement cur : vertices) {
			if (cur.equals(node)) {
				return cur;
			}
		}
		throw new RuntimeException("node not found: " + node);
	}

	public StructuralElementTypeRecognizer getStructuralElementTypeRecognizer() {
		return structuralElementTypeRecognizer;
	}

	private String getPdfUri() {
		return getParsedDocument().getPdfUri();
	}

	private synchronized ParsedDocument getParsedDocument() {
		return parsedDocument;
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
		public synchronized StructuralElement apply(Node node) {
			String uri = String.format("%s/s%s", getParsedDocument()
					.getFilename(), node.getId());
			StructuralElement element = new StructuralElementImpl.Builder(uri
					.hashCode(), uri).name(node.getName()).build();
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
					logger
							.log(
									Level.SEVERE,
									String
											.format(
													"failed to find the page number for a segment %s on PDF: %s",
													element.getUri(),
													getPdfUri()));
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

}
