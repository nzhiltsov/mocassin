package unittest.info;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;

public class PredictedPairInfo {
	private MocassinOntologyClasses fromType;
	private MocassinOntologyClasses toType;
	private Reference reference;

	public PredictedPairInfo(MocassinOntologyClasses fromType,
			MocassinOntologyClasses toType) {

		this.fromType = fromType;
		this.toType = toType;
	}

	public MocassinOntologyClasses getFromType() {
		return fromType;
	}

	public MocassinOntologyClasses getToType() {
		return toType;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

}
