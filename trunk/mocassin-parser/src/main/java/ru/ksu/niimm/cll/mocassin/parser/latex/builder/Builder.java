package ru.ksu.niimm.cll.mocassin.parser.latex.builder;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.latex.LatexDocumentModel;

public interface Builder {

	public abstract List<Edge<Node, Node>> analyze(LatexDocumentModel model);

}