package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.parser.arxmliv.xpath.impl.ArxmlivStructureElementTypes;

/**
 * Structural element is an element that corresponds to rhetorical structure
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
	 * @return label value of element to make references to it
	 */
	String getLabel();

	void setLabel(String label);

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
	List<String> getTitleTokens();

	void setTitleTokens(List<String> title);
}
