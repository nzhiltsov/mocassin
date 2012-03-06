package ru.ksu.niimm.cll.mocassin.nlp;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import edu.uci.ics.jung.graph.Graph;

/**
 * Represents a functionality of searching the structural elements in parsed
 * documents
 * 
 * @author nzhiltsov
 * 
 */
public interface StructuralElementSearcher {
	/**
	 * retrieve all the structural element of a given document
	 * 
	 * @param document
	 * @return
	 */
	List<StructuralElement> retrieveElements(ParsedDocument document);

	/**
	 * returns element by id for a given document
	 * 
	 * @param document
	 * @param id
	 * @return null if the element is not found
	 */
	StructuralElement findById(ParsedDocument document, int id);

	/**
	 * returns the closest predecessor of element with given id, filtering by
	 * its type name if necessary
	 * 
	 * @param document
	 * @param id
	 * @param filterTypes
	 * @return
	 */
	StructuralElement findClosestPredecessor(ParsedDocument document, int id,
			MocassinOntologyClasses... filterPredecessorTypes);

	StructuralElement findClosestPredecessor(StructuralElement element,
			MocassinOntologyClasses[] validDomains,
			Graph<StructuralElement, Reference> graph);
}
