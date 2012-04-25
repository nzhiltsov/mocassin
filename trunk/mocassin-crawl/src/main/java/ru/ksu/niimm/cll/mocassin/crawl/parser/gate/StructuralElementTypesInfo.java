package ru.ksu.niimm.cll.mocassin.crawl.parser.gate;

import java.util.SortedMap;

import ru.ksu.niimm.cll.mocassin.crawl.parser.gate.Reference;

/**
 * Prediction model for a pair of structural element types based on string
 * similarity techniques
 * 
 * @author nzhiltsov
 * 
 */
public interface StructuralElementTypesInfo {
	/**
	 * return a reference associated with info
	 * 
	 * @return
	 */
	Reference getReference();

	/**
	 * return structural type similarity vector for 'from' element of a reference
	 * 
	 * @return
	 */
	SortedMap<String, Float> getFromElementVector();

	/**
	 * return structural type similarity vector for 'to' element of a reference
	 * 
	 * @return
	 */
	SortedMap<String, Float> getToElementVector();
}
