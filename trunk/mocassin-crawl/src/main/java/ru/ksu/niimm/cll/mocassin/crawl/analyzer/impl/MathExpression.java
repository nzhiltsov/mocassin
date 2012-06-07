package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

/**
 * The class represents math expression in the text, i.e. variables and
 * formulas.
 * 
 * @author Nikita Zhiltsov
 * 
 */
public abstract class MathExpression {

    protected final int id;

    protected final String uri;
    /**
     * LaTeX representation
     */
    protected final String tex;

    protected MathExpression(int id, String uri, String tex) {
	this.id = id;
	this.uri = uri;
	this.tex = tex;
    }

    public int getId() {
	return id;
    }

    public String getTex() {
	return tex;
    }

    public String getUri() {
	return uri;
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
	Variable other = (Variable) obj;
	if (id != other.id)
	    return false;
	return true;
    }

}