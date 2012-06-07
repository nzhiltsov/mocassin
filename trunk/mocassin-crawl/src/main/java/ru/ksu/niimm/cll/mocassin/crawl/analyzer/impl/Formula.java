package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import java.util.List;

/**
 * The class represents mathematical formulas.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class Formula extends MathExpression {
    /**
     * Containing variables
     */
    private final List<Variable> variables;

    public Formula(int id, String uri, String tex, List<Variable> variables) {
	super(id, uri, tex);
	this.variables = variables;
    }

    public List<Variable> getVariables() {
	return variables;
    }
}
