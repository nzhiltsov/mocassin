package ru.ksu.niimm.ose.ontology.mock;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyFacade;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;

public class QueryManagerMock implements QueryManagerFacade {

	@Override
	public Model describe(String resourceUri) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

	@Override
	public String generateQuery(QueryStatement queryStatement) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

	@Override
	public List<OntologyResource> query(QueryStatement queryStatement) {
		List<OntologyResource> resources = new ArrayList<OntologyResource>();
		resources.add(new OntologyResource("http://arxiv.org/abs/math/0205001v1"));
		return resources;
	}

	@Override
	public List<Resource> query(String queryString, String retrievedResourceKey) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}
	
}
