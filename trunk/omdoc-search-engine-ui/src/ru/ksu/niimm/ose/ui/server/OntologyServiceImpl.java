package ru.ksu.niimm.ose.ui.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocOntologyLoader;
import ru.ksu.niimm.ose.ontology.OMDocResourceLoader;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManager;
import ru.ksu.niimm.ose.ontology.QueryStatement;
import ru.ksu.niimm.ose.ontology.RDFStorageLoader;
import ru.ksu.niimm.ose.ontology.impl.OMDocOntologyLoaderImpl;
import ru.ksu.niimm.ose.ontology.impl.OMDocResourceLoaderImpl;
import ru.ksu.niimm.ose.ontology.impl.QueryManagerImpl;
import ru.ksu.niimm.ose.ontology.impl.RDFStorageLoaderImpl;
import ru.ksu.niimm.ose.ui.client.OntConcept;
import ru.ksu.niimm.ose.ui.client.OntElement;
import ru.ksu.niimm.ose.ui.client.OntIndividual;
import ru.ksu.niimm.ose.ui.client.OntQueryStatement;
import ru.ksu.niimm.ose.ui.client.OntRelation;
import ru.ksu.niimm.ose.ui.client.OntTriple;
import ru.ksu.niimm.ose.ui.client.OntologyService;
import ru.ksu.niimm.ose.ui.client.ResultDescription;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OntologyServiceImpl extends RemoteServiceServlet implements
		OntologyService {
	private OMDocOntologyLoader omdocOntologyLoader = new OMDocOntologyLoaderImpl();
	private QueryManager queryManager = new QueryManagerImpl();
	private RDFStorageLoader storageLoader = new RDFStorageLoaderImpl();
	private OMDocResourceLoader omdocResourceLoader = new OMDocResourceLoaderImpl();

	public String greetServer(String input) {
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");
		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	@Override
	public List<OntConcept> getConceptList() {
		List<OntologyConcept> ontClassList = getOmdocOntologyLoader()
				.getOntClassList();
		List<OntConcept> targetConceptList = new ArrayList<OntConcept>();
		for (OntologyConcept concept : ontClassList) {
			OntConcept targetOntConcept = new OntConcept();
			targetOntConcept.setUri(concept.getUri());
			targetOntConcept.setLabel(concept.getLabel());
			targetConceptList.add(targetOntConcept);
		}
		Collections.sort(targetConceptList,
				new OntElement.OntElementComparator<OntConcept>());
		return targetConceptList;
	}

	public List<OntRelation> getRelationList(OntConcept concept) {
		OntologyConcept ontologyConcept = new OntologyConcept(concept.getUri(),
				concept.getLabel());
		List<OntologyRelation> ontologyPropertyList = getOmdocOntologyLoader()
				.getOntPropertyList(ontologyConcept);
		List<OntRelation> targetRelationList = new ArrayList<OntRelation>();
		for (OntologyRelation relation : ontologyPropertyList) {
			OntRelation targetRelation = new OntRelation();
			targetRelation.setUri(relation.getUri());
			targetRelation.setLabel(relation.getLabel());
			targetRelationList.add(targetRelation);
		}
		Collections.sort(targetRelationList,
				new OntElement.OntElementComparator<OntRelation>());
		return targetRelationList;
	}

	public List<OntElement> getRelationRangeConceptList(OntRelation relation) {
		OntologyRelation ontologyRelation = new OntologyRelation(relation
				.getUri(), relation.getLabel());
		List<OntologyConcept> ontologyRangeList = getOmdocOntologyLoader()
				.getOntPropertyRangeList(ontologyRelation);
		List<OntElement> targetConceptList = new ArrayList<OntElement>();
		for (OntologyConcept rangeConcept : ontologyRangeList) {
			OntConcept targetConcept = new OntConcept();
			targetConcept.setUri(rangeConcept.getUri());
			targetConcept.setLabel(rangeConcept.getLabel());
			targetConceptList.add(targetConcept);
			List<OntologyIndividual> individuals = getOmdocOntologyLoader()
					.getIndividuals(rangeConcept);
			for (OntologyIndividual individual : individuals) {
				targetConceptList.add(new OntIndividual(individual.getUri(),
						individual.getLabel()));
			}
		}

		Collections.sort(targetConceptList,
				new OntElement.OntElementComparator<OntElement>());
		return targetConceptList;
	}

	public List<ResultDescription> query(OntQueryStatement statement) {

		QueryStatement queryStatement = convertStatement(statement);
		List<OntologyResource> resources = getQueryManager().query(
				getStorageLoader().getRdfStorage(), queryStatement);
		List<OMDocElement> omdocElements = retriveOmdocElements(resources);
		List<ResultDescription> resultDescriptions = new ArrayList<ResultDescription>();
		for (OMDocElement omDocElement : omdocElements) {
			ResultDescription rd = new ResultDescription();
			rd.setLatexUri(omDocElement.getSrcRef().getFileName());
			rd.setPdfUri(omDocElement.getPdfFileName());
			rd.setAuthor(omDocElement.getArticleMetadata().getAuthor());
			rd.setTitle(omDocElement.getArticleMetadata().getTitle());
			rd.setRelevantContextString(String.format("line: %d, column: %d",
					omDocElement.getSrcRef().getLine(), omDocElement
							.getSrcRef().getColumn()));
			resultDescriptions.add(rd);
		}
		return resultDescriptions;
	}

	/**
	 * convert query statement representation by DTO to query statement
	 * representation by 'ontology' module classes
	 * 
	 * @param statement
	 *            query statement representation by DTO
	 * @return
	 */
	private QueryStatement convertStatement(OntQueryStatement statement) {
		List<OntologyTriple> retrievedTriples = new ArrayList<OntologyTriple>();
		List<OntTriple> ontStatementTriples = statement.getTriples();
		for (OntTriple ontTriple : ontStatementTriples) {
			OntologyConcept subject = new OntologyConcept(ontTriple
					.getSubject().getUri(), ontTriple.getSubject().getLabel());
			subject.setId(ontTriple.getSubject().getId());
			OntologyRelation predicate = new OntologyRelation(ontTriple
					.getPredicate().getUri(), ontTriple.getPredicate()
					.getLabel());
			predicate.setId(ontTriple.getPredicate().getId());
			OntologyElement object;
			if (ontTriple.getObject() instanceof OntConcept) {
				object = new OntologyConcept(ontTriple.getObject().getUri(),
						ontTriple.getObject().getLabel());
			} else {
				object = new OntologyIndividual(ontTriple.getObject().getUri(),
						ontTriple.getObject().getLabel());
			}
			object.setId(ontTriple.getObject().getId());
			OntologyTriple ontologyTriple = new OntologyTriple(subject,
					predicate, object);
			retrievedTriples.add(ontologyTriple);
		}
		QueryStatement queryStatement = new QueryStatement(retrievedTriples);
		return queryStatement;
	}

	/**
	 * retrieve the list of omdoc elements for given list of resources
	 * 
	 * @param resources
	 * @return
	 */
	private List<OMDocElement> retriveOmdocElements(
			List<OntologyResource> resources) {
		List<OMDocElement> elements = new ArrayList<OMDocElement>();
		for (OntologyResource resource : resources) {
			OMDocElement omdocElement = getOmdocResourceLoader().load(resource);
			elements.add(omdocElement);
		}
		return elements;
	}

	public OMDocOntologyLoader getOmdocOntologyLoader() {
		return omdocOntologyLoader;
	}

	public void setOmdocOntologyLoader(OMDocOntologyLoader loader) {
		this.omdocOntologyLoader = loader;
	}

	public QueryManager getQueryManager() {
		return queryManager;
	}

	public void setQueryManager(QueryManager queryManager) {
		this.queryManager = queryManager;
	}

	public RDFStorageLoader getStorageLoader() {
		return storageLoader;
	}

	public void setStorageLoader(RDFStorageLoader storageLoader) {
		this.storageLoader = storageLoader;
	}

	public OMDocResourceLoader getOmdocResourceLoader() {
		return omdocResourceLoader;
	}

	public void setOmdocResourceLoader(OMDocResourceLoader omdocResourceLoader) {
		this.omdocResourceLoader = omdocResourceLoader;
	}

}
