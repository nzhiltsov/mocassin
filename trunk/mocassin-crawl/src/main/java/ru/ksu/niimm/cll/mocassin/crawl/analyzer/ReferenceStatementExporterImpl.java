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
package ru.ksu.niimm.cll.mocassin.crawl.analyzer;

import static java.lang.String.format;
import static ru.ksu.niimm.cll.mocassin.rdf.ontology.model.URIConstants.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openrdf.model.Statement;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Formula;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.MathExpression;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Term;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl.Variable;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;
import edu.uci.ics.jung.graph.Graph;

/**
 * The class implements exporting the structural graph into RDF statements
 * 
 * @author Nikita Zhiltsov
 * 
 */
class ReferenceStatementExporterImpl implements ReferenceStatementExporter {
    private static final String EMPTY_STRING = "";
    private static final String END_LINE = "\n";
    private static final String SPACE = " ";
    private static final String SALT_SCHEMA = "http://salt.semanticauthoring.org/ontologies/sdo";
    private static final String SDO_PUBLICATION_TYPE = format("%s#Publication",
	    SALT_SCHEMA);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Statement> export(Graph<StructuralElement, Reference> graph) {
	Set<Statement> triples = new HashSet<Statement>();
	boolean addedPublication = false;
	for (Reference ref : graph.getEdges()) {
	    if (!addedPublication) {
		triples.add(createRdfTypeTriple(ref.getDocument().getUri(),
			SDO_PUBLICATION_TYPE));
		addedPublication = true;
	    }

	    StructuralElement from = graph.getSource(ref);
	    StructuralElement to = graph.getDest(ref);

	    triples.add(createTypeTriple(from));
	    triples.add(createTypeTriple(to));

	    if (from.getTitle() != null) {
		triples.add(createTitleTriple(from));
	    }
	    if (to.getTitle() != null) {
		triples.add(createTitleTriple(to));
	    }

	    Statement fromValueTriple = createValueTriple(from);
	    if (fromValueTriple != null) {
		triples.add(fromValueTriple);
	    }
	    Statement toValueTriple = createValueTriple(to);
	    if (toValueTriple != null) {
		triples.add(toValueTriple);
	    }
	    triples.add(createPageNumberTriple(from));
	    triples.add(createPageNumberTriple(to));

	    triples.add(createTriple(from.getUri(), ref.getPredictedRelation()
		    .getUri(), to.getUri()));
	    triples.add(createTriple(ref.getDocument().getUri(),
		    MocassinOntologyRelations.HAS_SEGMENT.getUri(),
		    from.getUri()));
	    triples.add(createTriple(ref.getDocument().getUri(),
		    MocassinOntologyRelations.HAS_SEGMENT.getUri(), to.getUri()));
	    triples.addAll(createTermTriples(from));
	    triples.addAll(createTermTriples(to));
	}
	return new ArrayList<Statement>(triples);
    }

    private static Statement createTypeTriple(StructuralElement element) {
	return createRdfTypeTriple(element.getUri(),
		MocassinOntologyClasses.getUri(element.getPredictedClass()));
    }

    private static Statement createTitleTriple(StructuralElement element) {
	return createLiteralTriple(element.getUri(),
		MocassinOntologyRelations.HAS_TITLE.getUri(),
		element.getTitle());
    }

    private static Statement createPageNumberTriple(StructuralElement element) {
	return createIntegerTriple(element.getUri(),
		MocassinOntologyRelations.HAS_START_PAGE_NUMBER.getUri(),
		element.getStartPageNumber());
    }

    private static Statement createValueTriple(StructuralElement element) {
	List<String> contents = element.getContents();
	if (contents == null)
	    return null;
	StringBuffer sb = new StringBuffer();
	for (String str : contents) {
	    sb.append(str);
	    sb.append(SPACE);
	}
	String textContents = sb.toString().replace(END_LINE, EMPTY_STRING);
	return createLiteralTriple(element.getUri(),
		MocassinOntologyRelations.HAS_TEXT.getUri(), textContents);
    }

    private static List<Statement> createTermTriples(StructuralElement element) {
	List<Statement> statements = new ArrayList<Statement>();
	List<Term> terms = element.getTerms();
	for (Term term : terms) {
	    statements
		    .add(createTriple(element.getUri(),
			    MocassinOntologyRelations.MENTIONS.getUri(),
			    term.getUri()));
	    statements.add(createRdfTypeTriple(term.getUri(),
		    term.getClassUri()));
	    statements.add(createRdfsLabelTriple(term.getUri(),
		    term.getInitialView()));
	    List<MathExpression> relatedMathExpressions = term
		    .getMathExpressions();
	    for (MathExpression expression : relatedMathExpressions) {
		statements.add(createTriple(term.getUri(),
			MocassinOntologyRelations.HAS_NOTATION.getUri(),
			expression.getUri()));
		String expressionTypeUri = expression instanceof Formula ? MocassinOntologyClasses
			.getUri(MocassinOntologyClasses.FORMULA)
			: MocassinOntologyClasses
				.getUri(MocassinOntologyClasses.VARIABLE);
		statements.add(createRdfTypeTriple(expression.getUri(),
			expressionTypeUri));
		statements.add(createLiteralTriple(expression.getUri(),
			MocassinOntologyRelations.HAS_LATEX_SOURCE.getUri(),
			expression.getTex()));
		if (expression instanceof Formula) {
		    Formula formula = (Formula) expression;
		    List<Variable> variables = formula.getVariables();
		    for (Variable variable : variables) {
			statements.add(createTriple(formula.getUri(),
				MocassinOntologyRelations.HAS_PART.getUri(),
				variable.getUri()));
			statements
				.add(createRdfTypeTriple(
					variable.getUri(),
					MocassinOntologyClasses
						.getUri(MocassinOntologyClasses.VARIABLE)));
			statements.add(createLiteralTriple(variable.getUri(),
				MocassinOntologyRelations.HAS_LATEX_SOURCE
					.getUri(), variable.getTex()));
		    }
		}
	    }
	}
	return statements;
    }
}
