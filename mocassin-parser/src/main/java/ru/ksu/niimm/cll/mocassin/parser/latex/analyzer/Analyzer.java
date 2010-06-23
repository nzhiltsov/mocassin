package ru.ksu.niimm.cll.mocassin.parser.latex.analyzer;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

public interface Analyzer {
	List<Edge<Node, Node>> analyze(LatexDocumentModel model);
}
