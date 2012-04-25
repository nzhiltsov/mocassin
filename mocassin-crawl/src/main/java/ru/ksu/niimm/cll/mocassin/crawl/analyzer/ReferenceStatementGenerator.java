package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import java.util.List;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.StructuralElement;

import edu.uci.ics.jung.graph.Graph;

public interface ReferenceStatementGenerator {
	List<Statement> convert(Graph<StructuralElement, Reference> references);
}
