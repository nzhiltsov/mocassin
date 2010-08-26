package ru.ksu.niimm.cll.mocassin.parser;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ru.ksu.niimm.cll.mocassin.parser.impl.adapters.EdgeAdapter;

/**
 * Relation between document parts
 * 
 * @author nzhiltsov
 * 
 */
@XmlJavaTypeAdapter(EdgeAdapter.class)
public interface Edge<From extends Node, To extends Node> {
	void connect(From from, To to, EdgeContext context);

	EdgeContext getContext();

	Node getFrom();

	Node getTo();
}
