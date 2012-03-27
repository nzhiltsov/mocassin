package ru.ksu.niimm.cll.mocassin.ui.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyBlankNode;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyConcept;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyElement;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyIndividual;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyLiteral;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyRelation;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResource;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyResourceFacade;
import ru.ksu.niimm.cll.mocassin.ontology.OntologyTriple;
import ru.ksu.niimm.cll.mocassin.ontology.QueryManagerFacade;
import ru.ksu.niimm.cll.mocassin.ontology.QueryStatement;
import ru.ksu.niimm.cll.mocassin.ui.client.OntBlankNode;
import ru.ksu.niimm.cll.mocassin.ui.client.OntConcept;
import ru.ksu.niimm.cll.mocassin.ui.client.OntLiteral;
import ru.ksu.niimm.cll.mocassin.ui.client.OntQueryStatement;
import ru.ksu.niimm.cll.mocassin.ui.client.OntRelation;
import ru.ksu.niimm.cll.mocassin.ui.client.OntTriple;
import ru.ksu.niimm.cll.mocassin.ui.client.QueryService;
import ru.ksu.niimm.cll.mocassin.ui.client.ResultDescription;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.ui.common.client.PagingLoadInfo;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

public class QueryServiceImpl implements QueryService {
	private final QueryManagerFacade queryManager;
	private final OntologyResourceFacade ontologyResourceFacade;

	@Inject
	public QueryServiceImpl(QueryManagerFacade queryManagerFacade,
			OntologyResourceFacade ontologyResourceFacade) {
		this.queryManager = queryManagerFacade;
		this.ontologyResourceFacade = ontologyResourceFacade;
	}

	public PagingLoadInfo<ResultDescription> query(OntQueryStatement statement,
			PagingLoadConfig pagingLoadConfig) {

		QueryStatement queryStatement = convertStatement(statement);
		List<OntologyResource> resources = getQueryManager().query(
				queryStatement);
		List<OntologyResource> filteredResources = filterResources(resources,
				pagingLoadConfig);
		List<ArticleMetadata> ontologyElements = retriveOntologyElements(filteredResources);
		List<ResultDescription> resultDescriptions = convertToResultDescriptions(ontologyElements);
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
				adjustedPagingLoadConfig.getOffset(),
				adjustedPagingLoadConfig.getOffset()
						+ adjustedPagingLoadConfig.getLimit());
		return filteredResources;
	}

	private List<ResultDescription> convertToResultDescriptions(
			List<ArticleMetadata> ontologyElements) {
		List<ResultDescription> resultDescriptions = new ArrayList<ResultDescription>();
		for (ArticleMetadata ontologyElement : ontologyElements) {
			ResultDescription rd = new ResultDescription();
			rd.setDocumentUri(ontologyElement.getId());
			rd.setViewerUri(ontologyElement.getCurrentSegmentUri() != null ? ontologyElement
					.getCurrentSegmentUri() : ontologyElement.getId());
			List<Link> links = ontologyElement.getLinks();

			Link pdfLink = Iterables.find(links, new Link.PdfLinkPredicate(),
					Link.nullPdfLink());
			rd.setPdfUri(pdfLink.getHref());
			Set<Author> authors = ontologyElement.getAuthors();
			List<String> authorNames = new ArrayList<String>();
			for (Author author : authors) {
				authorNames.add(author.getName());
			}
			rd.setAuthors(authorNames);
			String articleTitle = ontologyElement.getCurrentSegmentTitle() != null ? String
					.format("%s (%s)", ontologyElement.getTitle(),
							ontologyElement.getCurrentSegmentTitle())
					: ontologyElement.getTitle();
			rd.setTitle(articleTitle);
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
			OntologyConcept subject = convertSubject(ontTriple);
			OntologyElement predicate = convertPredicate(ontTriple);
			OntologyElement object = convertObject(ontTriple);
			OntologyTriple ontologyTriple = new OntologyTriple(subject,
					predicate, object);
			retrievedTriples.add(ontologyTriple);
		}
		QueryStatement queryStatement = new QueryStatement(retrievedTriples);
		queryStatement.setInferenceOn(statement.isInferenceOn());
		return queryStatement;
	}

	private OntologyElement convertObject(OntTriple ontTriple) {
		OntologyElement object;
		if (ontTriple.getObject() instanceof OntConcept) {
			object = new OntologyConcept(ontTriple.getObject().getUri(),
					ontTriple.getObject().getLabel());
		} else if (ontTriple.getObject() instanceof OntLiteral) {
			object = new OntologyLiteral(ontTriple.getObject().getLabel());
		} else if (ontTriple.getObject() instanceof OntBlankNode) {
			object = new OntologyBlankNode();
		} else {
			object = new OntologyIndividual(ontTriple.getObject().getUri(),
					ontTriple.getObject().getLabel());
		}
		object.setId(ontTriple.getObject().getId());
		return object;
	}

	private OntologyElement convertPredicate(OntTriple ontTriple) {
		OntologyElement predicate;
		if (ontTriple.getPredicate() instanceof OntRelation) {
			predicate = new OntologyRelation(ontTriple.getPredicate().getUri(),
					ontTriple.getPredicate().getLabel());
		} else {
			predicate = new OntologyBlankNode();
		}
		predicate.setId(ontTriple.getPredicate().getId());
		return predicate;
	}

	private OntologyConcept convertSubject(OntTriple ontTriple) {
		OntologyConcept subject = new OntologyConcept(ontTriple.getSubject()
				.getUri(), ontTriple.getSubject().getLabel());
		subject.setId(ontTriple.getSubject().getId());
		return subject;
	}

	/**
	 * retrieve the list of ontology elements for given list of resources
	 * 
	 * @param resources
	 * @return
	 */
	private List<ArticleMetadata> retriveOntologyElements(
			List<OntologyResource> resources) {
		List<ArticleMetadata> elements = new ArrayList<ArticleMetadata>();
		for (OntologyResource resource : resources) {
			ArticleMetadata ontologyElement = getOntologyResourceFacade().load(
					resource);
			if (ontologyElement != null && ontologyElement.getTitle() != null) {
				elements.add(ontologyElement);
			}
		}
		return elements;
	}

	public QueryManagerFacade getQueryManager() {
		return queryManager;
	}

	public OntologyResourceFacade getOntologyResourceFacade() {
		return ontologyResourceFacade;
	}

}
