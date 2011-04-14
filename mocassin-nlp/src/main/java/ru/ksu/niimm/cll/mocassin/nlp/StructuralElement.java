package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;

/**
 * Structural element is an element that belongs to the rhetorical structure
 * (section, subsection) or mathematical structure (theorem, proof) of a
 * document
 * 
 * @see ArxmlivStructureElementTypes
 * 
 * @author nzhiltsov
 * 
 */
public interface StructuralElement {
	/**
	 * 
	 * @return identifier
	 */
	int getId();
	/**
	 * 
	 * @return URI
	 */
	String getUri();

	/**
	 * 
	 * @return start offset
	 */
	long getStart();

	/**
	 * 
	 * @return end offset
	 */
	long getEnd();

	/**
	 * 
	 * @return label values of an element to make references to it
	 */
	List<String> getLabels();

	void setLabels(List<String> labels);

	/**
	 * 
	 * @return name of element according to its type (e.g. 'section', 'thm',
	 *         'example')
	 */
	String getName();

	/**
	 * 
	 * @return tokens of a title (e.g. 'Introduction', 'Theorem 1.1'), return
	 *         null if the title is absent
	 */
	List<Token> getTitleTokens();

	/**
	 * 
	 * @return title tokens as a string
	 */
	String toTitleString();

	void setTitleTokens(List<Token> title);

	void setPredictedClass(MocassinOntologyClasses clazz);

	MocassinOntologyClasses getPredictedClass();
}