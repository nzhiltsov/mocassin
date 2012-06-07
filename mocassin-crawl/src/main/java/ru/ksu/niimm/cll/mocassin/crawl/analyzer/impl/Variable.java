package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

/**
 * The class represents recognized mathematical variables.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class Variable extends MathExpression {

    public Variable(int id, String uri, String tex) {
	super(id, uri, tex);
    }

}
