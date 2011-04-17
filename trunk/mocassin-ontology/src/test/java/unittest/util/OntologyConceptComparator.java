package unittest.util;

import java.io.Serializable;
import java.util.Comparator;

import ru.ksu.niimm.ose.ontology.OntologyConcept;

@SuppressWarnings("serial")
public class OntologyConceptComparator implements Serializable, Comparator<OntologyConcept> {

	@Override
	public int compare(OntologyConcept o1, OntologyConcept o2) {
		return o1.getLabel().compareTo(o2.getLabel());
	}

}