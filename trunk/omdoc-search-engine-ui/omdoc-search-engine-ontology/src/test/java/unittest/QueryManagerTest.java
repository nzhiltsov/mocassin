package unittest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManager;
import ru.ksu.niimm.ose.ontology.QueryStatement;
import ru.ksu.niimm.ose.ontology.RDFStorageLoader;
import ru.ksu.niimm.ose.ontology.impl.QueryManagerImpl;
import ru.ksu.niimm.ose.ontology.impl.RDFStorageLoaderImpl;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;

public class QueryManagerTest {
	private QueryManager queryManager;
	private RDFStorageLoader rdfStorageLoader;

	@Before
	public void setup() {
		queryManager = new QueryManagerImpl();
		rdfStorageLoader = new RDFStorageLoaderImpl();
	}

	@Test
	public void testQuery() {
		OntModel rdfStorage = rdfStorageLoader.getRdfStorage();
		List<Resource> resources = queryManager
				.query(
						rdfStorage,
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
								+ "SELECT * WHERE "
								+ "{ "
								+ "?1 rdf:type <http://omdoc.org/ontology#Lemma> . ?1 <http://omdoc.org/ontology#hasProperty> ?2 ."
								+ "?2 rdf:type <http://omdoc.org/ontology#Property> . ?2 <http://omdoc.org/ontology#usesSymbol> <http://www.openmath.org/cd/latexml#set> ."
								+ "}", "?1");
		resources.size();
	}

	@Test
	public void testQueryStatement() {
		List<OntologyTriple> retrievedTriples = new ArrayList<OntologyTriple>();
		OntologyRelation predicate = new OntologyRelation(
				"http://omdoc.org/ontology#hasProperty", "hasProperty");
		OntologyConcept subject = new OntologyConcept(
				"http://omdoc.org/ontology#Lemma", "lemma");
		OntologyConcept object = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		retrievedTriples.add(new OntologyTriple(subject, predicate, object));
		OntologyRelation predicate2 = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "usesSymbol");
		OntologyConcept subject2 = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		OntologyIndividual object2 = new OntologyIndividual(
				"http://www.openmath.org/cd/latexml#set", "set");
		retrievedTriples.add(new OntologyTriple(subject2, predicate2, object2));
		QueryStatement queryStatement = new QueryStatement(retrievedTriples);
		OntModel rdfStorage = rdfStorageLoader.getRdfStorage();
		List<OntologyResource> resources = queryManager.query(rdfStorage,
				queryStatement);
		resources.size();
	}
}
