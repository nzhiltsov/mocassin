package ru.ksu.niimm.cll.mocassin.analyzer.location;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;

/**
 * The interface represents characteristics related to mutual location of
 * structural elements of a given reference
 * 
 * @author nzhiltsov
 * 
 */
public interface ReferenceElementLocationInfo {
	/**
	 * return reference for which the distances are computed
	 * 
	 * @return
	 */
	Reference getReference();

	/**
	 * distance between start points of reference's structural elements
	 * normalized by document length
	 * 
	 * @return
	 */
	float getStartDistance();

	/**
	 * distance between end points of reference's structural elements normalized
	 * by document length
	 * 
	 * @return
	 */
	float getEndDistance();
}
