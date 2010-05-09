package ru.ksu.niimm.cll.mocassin.ui.server;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.client.OntConcept;
import ru.ksu.niimm.cll.mocassin.ui.client.OntLiteral;
import ru.ksu.niimm.cll.mocassin.ui.client.OntQueryStatement;
import ru.ksu.niimm.cll.mocassin.ui.client.OntTriple;
import ru.ksu.niimm.cll.mocassin.ui.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.client.PagingLoadInfo;
import ru.ksu.niimm.cll.mocassin.ui.client.QueryService;
import ru.ksu.niimm.cll.mocassin.ui.client.ResultDescription;
import ru.ksu.niimm.ose.ontology.OMDocElement;
import ru.ksu.niimm.ose.ontology.OMDocResourceFacade;
import ru.ksu.niimm.ose.ontology.OntologyConcept;
import ru.ksu.niimm.ose.ontology.OntologyElement;
import ru.ksu.niimm.ose.ontology.OntologyIndividual;
import ru.ksu.niimm.ose.ontology.OntologyLiteral;
import ru.ksu.niimm.ose.ontology.OntologyRelation;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyTriple;
import ru.ksu.niimm.ose.ontology.QueryManagerFacade;
import ru.ksu.niimm.ose.ontology.QueryStatement;

import com.google.inject.Inject;

public class QueryServiceImpl implements QueryService {
	private QueryManagerFacade queryManager;
	private OMDocResourceFacade omdocResourceFacade;

	@Inject
	public QueryServiceImpl(QueryManagerFacade queryManagerFacade,
			OMDocResourceFacade omdocResourceFacade) {
		this.queryManager = queryManagerFacade;
		this.omdocResourceFacade = omdocResourceFacade;
	}

	public PagingLoadInfo<ResultDescription> query(OntQueryStatement statement,
			PagingLoadConfig pagingLoadConfig) {

		QueryStatement queryStatement = convertStatement(statement);
		List<OntologyResource> resources = getQueryManager().query(
				queryStatement);
		List<OntologyResource> filteredResources = filterResources(resources,
				pagingLoadConfig);
		List<OMDocElement> omdocElements = retriveOmdocElements(filteredResources);
		List<ResultDescription> resultDescriptions = convertToResultDescriptions(omdocElements);
		PagingLoadInfo<ResultDescription> pagingLoadInfo = new PagingLoadInfo<ResultDescription>();
		pagingLoadInfo.setPagingLoadConfig(pagingLoadConfig);
		pagingLoadInfo.setData(resultDescriptions);
		pagingLoadInfo.setFullCollectionSize(resources.size());
		return pagingLoadInfo;
	}

	/**
	 * filter resources according to given paging load config
	 * 
	 * @param resources
	 * @param pagingLoadConfig
	 * @return
	 */
	private List<OntologyResource> filterResources(
			List<OntologyResource> resources, PagingLoadConfig pagingLoadConfig) {
		if (resources.isEmpty()) {
			return resources;
		}
		PagingLoadConfig adjustedPagingLoadConfig = PagingLoadConfig
				.adjustPagingLoadConfig(pagingLoadConfig, resources.size());
		List<OntologyResource> filteredResources = resources.subList(
				adjustedPagingLoadConfig.getOffset(), adjustedPagingLoadConfig
						.getOffset()
						+ adjustedPagingLoadConfig.getLimit());
		return filteredResources;
	}

	private List<ResultDescription> convertToResultDescriptions(
			List<OMDocElement> omdocElements) {
		List<ResultDescription> resultDescriptions = new ArrayList<ResultDescription>();
		for (OMDocElement omDocElement : omdocElements) {
			ResultDescription rd = new ResultDescription();
			rd.setDocumentUri(omDocElement.getResourceUri());
			rd.setLatexUri(omDocElement.getSrcRef().getFileName());
			rd.setPdfUri(omDocElement.getPdfFileName());
			rd.setAuthors(omDocElement.getArticleMetadata().getAuthors());
			String articleTitle = omDocElement.getArticleMetadata().getTitle();
			String title = !isEmpty(articleTitle) ? articleTitle : omDocElement
					.getArticleMetadata().getUri();
			rd.setTitle(title);
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
			} else if (ontTriple.getObject() instanceof OntLiteral) {
				object = new OntologyLiteral(ontTriple.getObject().getLabel());
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
			OMDocElement omdocElement = getOmdocResourceFacade().load(resource);
			elements.add(omdocElement);
		}
		return elements;
	}

	public QueryManagerFacade getQueryManager() {
		return queryManager;
	}

	public OMDocResourceFacade getOmdocResourceFacade() {
		return omdocResourceFacade;
	}

	private boolean isEmpty(String text) {
		return text == null || text.equals("");
	}
}
