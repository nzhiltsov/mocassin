package ru.ksu.niimm.cll.mocassin.ui.dashboard.server;

import java.util.List;
import java.util.logging.Logger;

import com.google.inject.Inject;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.ArxivDAOFacade;
import ru.ksu.niimm.cll.mocassin.nlp.latex.LatexStructuralElementSearcher;
import ru.ksu.niimm.cll.mocassin.nlp.util.ReferenceTripleUtil;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivArticleMetadata;
import ru.ksu.niimm.cll.mocassin.ui.dashboard.client.ArxivService;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

public class ArXMLivAdapterService implements ArxivService {
	@Inject
	private Logger logger;
	@Inject
	private ArxivDAOFacade arxivDAOFacade;
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;
	@Inject
	private LatexStructuralElementSearcher latexStructuralElementSearcher;
	@Inject
	private ReferenceTripleUtil referenceTripleUtil;

	@Override
	public void handle(String arxivId) {

		ArticleMetadata metadata = arxivDAOFacade.retrieve(arxivId);
		
	}

	@Override
	public List<ArxivArticleMetadata> loadArticles() {
		// TODO Auto-generated method stub
		return null;
	}

}
