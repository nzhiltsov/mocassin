package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;

import com.google.common.collect.ImmutableMap;

import edu.uci.ics.jung.graph.Graph;

public class MLNUtil {
    private static ImmutableMap<MocassinOntologyClasses, String> classMap;
    private static ImmutableMap<MocassinOntologyRelations, String> relationMap;
    private static ImmutableMap<MocassinOntologyRelations, String> ruleRelationMap;

    static {
	classMap = ImmutableMap.<MocassinOntologyClasses, String> builder()
		.put(MocassinOntologyClasses.AXIOM, "Axiom")
		.put(MocassinOntologyClasses.EQUATION, "Equation")
		.put(MocassinOntologyClasses.CLAIM, "Claim")
		.put(MocassinOntologyClasses.CONJECTURE, "Conjecture")
		.put(MocassinOntologyClasses.COROLLARY, "Corollary")
		.put(MocassinOntologyClasses.DEFINITION, "Definition")
		.put(MocassinOntologyClasses.EXAMPLE, "Example")
		.put(MocassinOntologyClasses.FIGURE, "Figure")
		.put(MocassinOntologyClasses.LEMMA, "Lemma")
		.put(MocassinOntologyClasses.PROOF, "Proof")
		.put(MocassinOntologyClasses.PROPOSITION, "Proposition")
		.put(MocassinOntologyClasses.REMARK, "Remark")
		.put(MocassinOntologyClasses.THEOREM, "Theorem")
		.put(MocassinOntologyClasses.SECTION, "Section")
		.put(MocassinOntologyClasses.TABLE, "Table").build();
	relationMap = ImmutableMap
		.<MocassinOntologyRelations, String> builder()
		.put(MocassinOntologyRelations.REFERS_TO, "refers_to")
		.put(MocassinOntologyRelations.DEPENDS_ON, "refers_to")
		.put(MocassinOntologyRelations.HAS_PART, "has_part")
		.put(MocassinOntologyRelations.FOLLOWED_BY, "followed_by")
		.build();
	ruleRelationMap = ImmutableMap
		.<MocassinOntologyRelations, String> builder()
		.put(MocassinOntologyRelations.REFERS_TO, "refers_to")
		.put(MocassinOntologyRelations.DEPENDS_ON, "depends_on")
		.put(MocassinOntologyRelations.EXEMPLIFIES, "exemplifies")
		.put(MocassinOntologyRelations.PROVES, "proves")
		.put(MocassinOntologyRelations.HAS_CONSEQUENCE,
			"has_consequence").build();
    }

    public static void generateMLNFile(BufferedWriter out,
	    Graph<StructuralElement, Reference> graph,
	    List<RelationFeatureInfo> features, String docId)
	    throws IOException {
	writeClasses(out,
		new ArrayList<StructuralElement>(graph.getVertices()), docId);
	out.write("\n");
	writeRelations(out, graph, new ArrayList<Reference>(graph.getEdges()),
		docId);
	out.write("\n");
//	writeFeautres(out, features, docId);

    }

    private static String upperFirstLetter(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }

    private static void writeClasses(BufferedWriter out,
	    List<StructuralElement> elements, String docId) throws IOException {
	for (StructuralElement element : elements) {
	    if (classMap.containsKey(element.getPredictedClass())) {
		out.write(classMap.get(element.getPredictedClass()) + "("
			+ upperFirstLetter(docId) + "_" + element.getId() + ")\n");
	    }
	}
    }

    private static void writeRelations(BufferedWriter out,
	    Graph<StructuralElement, Reference> graph,
	    List<Reference> relations, String docId) throws IOException {
	for (Reference reference : relations) {
	    if (relationMap.containsKey(reference.getPredictedRelation())) {
		out.write(relationMap.get(reference.getPredictedRelation())
			+ "(" + upperFirstLetter(docId) + "_"
			+ graph.getSource(reference).getId() + "," + upperFirstLetter(docId)
			+ "_" + graph.getDest(reference).getId() + ")\n");
	    }
	}
    }

    private static void writeFeautres(BufferedWriter out,
	    List<RelationFeatureInfo> features, String docId)
	    throws IOException {
	for (RelationFeatureInfo feature : features) {
	    out.write(featureLine("PAS", feature
		    .getPreferentialAttachmentScore(), docId, feature.getFrom()
		    .getId(), feature.getTo().getId()));
	    out.write(featureLine("Katz", feature.getKatzCoefficient(), docId,
		    feature.getFrom().getId(), feature.getTo().getId()));
	    out.write(featureLine("InverseKatz", feature
		    .getInverseKatzCoefficient(), docId, feature.getFrom()
		    .getId(), feature.getTo().getId()));
	    out.write(featureLine("Jaccard", feature
		    .getNeighborJaccardCoefficient(), docId, feature.getFrom()
		    .getId(), feature.getTo().getId()));
	    out.write("PageRank(" + upperFirstLetter(docId) + "_" + feature.getFrom().getId()
		    + ") " + String.format("%.10f", feature.getFromPR()) + "\n");
	    out.write("PageRank(" + upperFirstLetter(docId) + "_" + feature.getTo().getId()
		    + ") " + String.format("%.10f", feature.getToPR()) + "\n");
	}
    }

    private static String featureLine(String feature, float value,
	    String docId, int id1, int id2) {
	return feature + "(" + upperFirstLetter(docId) + "_" + id1 + "," + upperFirstLetter(docId) + "_" + id2
		+ ") " + String.format("%.10f", value) + "\n";
    }

    public static String generateDomainRangeRules(
	    List<MocassinOntologyClasses> domain,
	    List<MocassinOntologyClasses> range,
	    MocassinOntologyRelations relation) {

	StringBuilder builder = new StringBuilder();
	builder.append(ruleRelationMap.get(relation)).append("(x,y) => (");

	for (MocassinOntologyClasses ontClass : domain) {
	    if (classMap.containsKey(ontClass)) {
		builder.append(classMap.get(ontClass)).append("(x)")
			.append(" v ");
	    }
	}
	builder.delete(builder.length() - 3, builder.length());
	builder.append(") ^ (");

	for (MocassinOntologyClasses ontClass : range) {
	    if (classMap.containsKey(ontClass)) {
		builder.append(classMap.get(ontClass)).append("(y)")
			.append(" v ");
	    }
	}
	builder.delete(builder.length() - 3, builder.length());
	builder.append(").\n");
	return builder.toString();
    }
}
