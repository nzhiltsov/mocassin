package ru.ksu.niimm.cll.mocassin.parser;

/**
 * Relation between document parts
 * 
 * @author nzhiltsov
 * 
 */
public interface Edge<From extends Node, To extends Node> {
	void connect(From from, To to, EdgeContext context);

	EdgeContext getContext();

	Node getFrom();

	Node getTo();
}
