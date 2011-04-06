package ru.ksu.niimm.ose.ontology.mock;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

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
