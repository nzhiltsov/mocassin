package unittest.util;

import java.util.Comparator;

import ru.ksu.niimm.ose.ontology.OntologyConcept;

public class OntologyConceptComparator implements Comparator<OntologyConcept> {

	@Override
	public int compare(OntologyConcept o1, OntologyConcept o2) {
		return o1.getLabel().compareTo(o2.getLabel());
	}

}