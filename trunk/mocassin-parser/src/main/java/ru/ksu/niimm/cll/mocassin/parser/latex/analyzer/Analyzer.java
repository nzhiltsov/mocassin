package ru.ksu.niimm.cll.mocassin.parser.latex.analyzer;

import java.util.List;

import net.sourceforge.texlipse.model.OutlineNode;

import ru.ksu.niimm.cll.mocassin.parser.Edge;
import ru.ksu.niimm.cll.mocassin.parser.Node;

public interface Analyzer {
	List<Edge<Node, Node>> analyze(List<OutlineNode> tree);
}
