package unittest;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyModule;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;

import com.google.inject.Inject;
import com.hp.hpl.jena.rdf.model.Resource;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;

@RunWith(MycilaJunitRunner.class)
@GuiceContext(OntologyModule.class)
public class QueryManagerFacadeTest {
	@Inject
	private QueryManagerFacade queryManagerFacade;

	@Test
	public void testQuery() {
		List<Resource> resources = getQueryManagerFacade()
				.query(
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
								+ "SELECT * WHERE "
								+ "{ "
								+ "?1 rdf:type <http://omdoc.org/ontology#Lemma> . ?1 <http://omdoc.org/ontology#hasProperty> ?2 ."
								+ "?2 rdf:type <http://omdoc.org/ontology#Property> . ?2 <http://omdoc.org/ontology#usesSymbol> <http://www.openmath.org/cd/latexml#set> ."
								+ "}", "?1");
		resources.size();
	}

	@Test
	public void testSubclassofInferenceForQuery() {
		List<Resource> resources = getQueryManagerFacade()
				.query(
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
								+ "SELECT * WHERE "
								+ "{ "
								+ "?1 rdf:type <http://omdoc.org/ontology#Assertion> . ?1 <http://omdoc.org/ontology#hasProperty> ?2 ."
								+ "?2 rdf:type <http://omdoc.org/ontology#Property>."
								+ "}", "?1");
		resources.size();
	}

	@Test
	public void testQueryStatement() {
		QueryStatement queryStatement = makeExampleStatement();
		List<OntologyResource> resources = getQueryManagerFacade()
				.query(queryStatement);
		// TODO : add more non-trivial test case
		resources.size();
	}

	@Test
	public void testGenerateQuery() {
		QueryStatement queryStatement = makeExampleStatement();
		String queryString = getQueryManagerFacade().generateQuery(queryStatement);
		// TODO : add more check of equals to correct string
		Assert.assertTrue(queryString != null && !queryString.equals(""));
	}

	private QueryStatement makeExampleStatement() {
		List<OntologyTriple> retrievedTriples = new ArrayList<OntologyTriple>();
		OntologyRelation predicate = new OntologyRelation(
				"http://omdoc.org/ontology#hasProperty", "hasProperty");
		predicate.setId(3);
		OntologyConcept subject = new OntologyConcept(
				"http://omdoc.org/ontology#Lemma", "lemma");
		subject.setId(1);
		OntologyConcept object = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		object.setId(5);
		retrievedTriples.add(new OntologyTriple(subject, predicate, object));
		OntologyRelation predicate2 = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "usesSymbol");
		predicate2.setId(6);
		OntologyConcept subject2 = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		subject2.setId(5);
		OntologyIndividual object2 = new OntologyIndividual(
				"http://www.openmath.org/cd/latexml#set", "set");
		object2.setId(8);
		retrievedTriples.add(new OntologyTriple(subject2, predicate2, object2));
		OntologyRelation predicate3 = new OntologyRelation(
				"http://omdoc.org/ontology#usesSymbol", "usesSymbol");
		predicate.setId(7);
		OntologyConcept subject3 = new OntologyConcept(
				"http://omdoc.org/ontology#Property", "property");
		subject3.setId(5);
		OntologyIndividual object3 = new OntologyIndividual(
				"http://www.openmath.org/cd/relation1#eq", "eq");
		object3.setId(9);
		retrievedTriples.add(new OntologyTriple(subject3, predicate3, object3));
		OntologyRelation predicate4 = new OntologyRelation(
				"http://omdoc.org/ontology#homeTheory", "has home Theory");
		predicate4.setId(2);
		OntologyConcept subject4 = new OntologyConcept(
				"http://omdoc.org/ontology#Lemma", "lemma");
		subject4.setId(1);
		OntologyConcept object4 = new OntologyConcept(
				"http://omdoc.org/ontology#Theory", "theory");
		object4.setId(4);
		retrievedTriples.add(new OntologyTriple(subject4, predicate4, object4));

		QueryStatement queryStatement = new QueryStatement(retrievedTriples);
		return queryStatement;
	}

	public QueryManagerFacade getQueryManagerFacade() {
		return queryManagerFacade;
	}
	
}
