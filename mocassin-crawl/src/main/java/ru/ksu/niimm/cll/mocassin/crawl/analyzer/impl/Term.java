package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import java.util.List;

/**
 * The class represents a guessing about a mathematical term instance in terms
 * of some domain ontology (e.g. OntoMathPro) that occurs in the processing text
 * and is associated with some mathematical expression.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public class Term {
    private final int id;

    private final String uri;
    /**
     * URI of its class according to a domain ontology
     */
    private final String classUri;
    /**
     * normalized version of the term
     */
    private final String normalizedView;
    /**
     * original occurence in the text
     */
    private final String initialView;
    /**
     * The value must range from 0 to 1, and represents the guessing confidence
     */
    private final float confidenceScore;
    /**
     * Associated mathematical expressions
     */
    private final List<MathExpression> mathExpression;

    public Term(int id, String uri, String classUri, String normalizedView,
	    String initialView, float confidenceScore,
	    List<MathExpression> mathExpressions) {
	this.id = id;
	this.uri = uri;
	this.classUri = classUri;
	this.normalizedView = normalizedView;
	this.initialView = initialView;
	this.confidenceScore = confidenceScore;
	this.mathExpression = mathExpressions;
    }

    public int getId() {
	return id;
    }

    public String getUri() {
	return uri;
    }
    

    public String getClassUri() {
        return classUri;
    }

    public String getNormalizedView() {
	return normalizedView;
    }

    public String getInitialView() {
	return initialView;
    }

    public float getConfidenceScore() {
	return confidenceScore;
    }

    public List<MathExpression> getMathExpressions() {
	return mathExpression;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id;
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Term other = (Term) obj;
	if (id != other.id)
	    return false;
	return true;
    }

}
