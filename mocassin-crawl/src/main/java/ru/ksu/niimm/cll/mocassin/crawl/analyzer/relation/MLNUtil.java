package ru.ksu.niimm.cll.mocassin.crawl.analyzer.relation;

import com.google.common.collect.ImmutableMap;
import edu.uci.ics.jung.graph.Graph;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyClasses;
import ru.ksu.niimm.cll.mocassin.rdf.ontology.MocassinOntologyRelations;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MLNUtil {
    private ImmutableMap<MocassinOntologyClasses, String> classMap;
    private ImmutableMap<MocassinOntologyRelations, String> relationMap;

    public MLNUtil() {
        classMap = ImmutableMap
                .<MocassinOntologyClasses, String>builder()
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
                .put(MocassinOntologyClasses.TABLE, "Table")
                .build();
        relationMap = ImmutableMap
                .<MocassinOntologyRelations, String>builder()
                .put(MocassinOntologyRelations.REFERS_TO, "refers_to")
                .put(MocassinOntologyRelations.DEPENDS_ON, "refers_to")
                .put(MocassinOntologyRelations.HAS_PART, "has_part")
                .put(MocassinOntologyRelations.FOLLOWED_BY, "followed_by")
                .build();
    }

    public void generateMLNFile(BufferedWriter out, Graph<StructuralElement, Reference> graph,
                                List<RelationFeatureInfo> features, String docId) throws IOException {
        writeClasses(out, new ArrayList<StructuralElement>(graph.getVertices()), docId);
        out.write("\n");
        writeRelations(out, graph, new ArrayList<Reference>(graph.getEdges()), docId);
        out.write("\n");
        writeFeautres(out, features, docId);

    }

    private void writeClasses(BufferedWriter out, List<StructuralElement> elements,
                              String docId) throws IOException {
        for (StructuralElement element: elements) {
            if (classMap.containsKey(element.getPredictedClass())) {
                out.write(classMap.get(element.getPredictedClass())
                    + "(" + docId + "_" + element.getId() + ")\n");
            }
        }
    }

    private void writeRelations(BufferedWriter out, Graph<StructuralElement, Reference> graph,
                                List<Reference> relations, String docId) throws IOException {
        for (Reference reference: relations) {
            if (relationMap.containsKey(reference.getPredictedRelation())) {
                out.write(relationMap.get(reference.getPredictedRelation())
                    + "(" + docId + "_" + graph.getSource(reference).getId()
                    + "," + docId + "_" + graph.getDest(reference).getId() + ")\n");
            }
        }
    }

    private void writeFeautres(BufferedWriter out, List<RelationFeatureInfo> feautures,
                               String docId) throws IOException {
        for (RelationFeatureInfo feature: feautures) {
            out.write(featureLine("PAS", feature.getPreferentialAttachmentScore(),
                    docId, feature.getFrom().getId(), feature.getTo().getId()));
            out.write(featureLine("Katz", feature.getKatzCoefficient(),
                    docId, feature.getFrom().getId(), feature.getTo().getId()));
            out.write(featureLine("InverseKatz", feature.getInverseKatzCoefficient(),
                    docId, feature.getFrom().getId(), feature.getTo().getId()));
            out.write(featureLine("Jaccard", feature.getNeighborJaccardCoefficient(),
                    docId, feature.getFrom().getId(), feature.getTo().getId()));
            out.write("PageRank(" + docId + "_" + feature.getFrom().getId()
                + ") " + feature.getFromPR() + "\n");
            out.write("PageRank(" + docId + "_" + feature.getTo().getId()
                    + ") " + feature.getToPR() + "\n");
        }
    }

    private String featureLine(String feature, float value, String docId, int id1, int id2) {
        return feature + "(" + docId + "_" + id1
                + "," + docId + "_" + id2 + ") "
                + value + "\n";
    }
}
