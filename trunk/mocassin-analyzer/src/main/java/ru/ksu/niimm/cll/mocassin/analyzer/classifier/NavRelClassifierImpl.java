package ru.ksu.niimm.cll.mocassin.analyzer.classifier;

import static java.lang.String.format;

import org.slf4j.Logger;

import ru.ksu.niimm.cll.mocassin.nlp.Reference;
import ru.ksu.niimm.cll.mocassin.nlp.StructuralElement;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.ontology.MocassinOntologyRelations;
import ru.ksu.niimm.cll.mocassin.util.inject.log.InjectLogger;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.uci.ics.jung.graph.Graph;

public class NavRelClassifierImpl implements NavigationalRelationClassifier {
	private static final String ATTRIBUTE_NAME_DELIMITER = "_";

	@InjectLogger
	private Logger logger;

	private final Classifier classifier;

	private final Instances trainingSetHeader;

	@Inject
	private NavRelClassifierImpl(@Named("classifier") Classifier classifier,
			@Named("training.set.header") Instances trainingSetHeader) {
		this.classifier = classifier;
		this.trainingSetHeader = trainingSetHeader;
	}

	@Override
	public Prediction predict(Reference reference,
			Graph<StructuralElement, Reference> graph) {
		StructuralElement from = graph.getSource(reference);
		MocassinOntologyClasses fromType = from.getPredictedClass();
		StructuralElement to = graph.getDest(reference);
		MocassinOntologyClasses toType = to.getPredictedClass();
		long documentSize = reference.getDocument().getSize();
		float normalizedStartDistance = ((float) from.getGateStartOffset() - to
				.getGateStartOffset()) / documentSize;

		float normalizedEndDistance = ((float) from.getGateEndOffset() - to
				.getGateEndOffset()) / documentSize;
		Instance instance = new Instance(trainingSetHeader.numAttributes());
		instance.setDataset(trainingSetHeader);
		instance.setValue(0, fromType.toString());
		instance.setValue(1, toType.toString());
		instance.setValue(2, normalizedStartDistance);
		instance.setValue(3, normalizedEndDistance);
		for (int i = 4; i < trainingSetHeader.numAttributes() - 1; i++) {
			String attrName = trainingSetHeader.attribute(i).name();
			String word = attrName.substring(attrName.indexOf(ATTRIBUTE_NAME_DELIMITER) + 1);
			instance.setValue(i,
					reference.getSentenceTokens().contains(word) ? 1 : 0);
		}
		try {
			double[] distribution = classifier
					.distributionForInstance(instance);
			Prediction prediction;
			if (distribution[0] > distribution[1]) {
				prediction = new Prediction(
						MocassinOntologyRelations.REFERS_TO, distribution[0]);
			} else {
				prediction = new Prediction(
						MocassinOntologyRelations.DEPENDS_ON, distribution[1]);
			}
			return prediction;
		} catch (Exception e) {
			logger.error(
					"Couldn't classify a reference with id='{}' in a document='{}'; null will be returned",
					format("%d/%s", reference.getId(),  reference.getDocument()
							.getUri()), e);
			return null;
		}
	}
}
