package ru.ksu.niimm.cll.mocassin.analyzer;

import java.util.List;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import edu.uci.ics.jung.graph.Graph;

public interface ReferenceStatementGenerator {
	List<Statement> convert(Graph<StructuralElement, Reference> references);
}
