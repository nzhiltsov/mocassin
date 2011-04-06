package ru.ksu.niimm.cll.mocassin.ui.viewer.server;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ArticleInfo;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.ViewerService;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

import com.google.inject.Inject;

public class ViewerServiceImpl implements ViewerService {
	@Inject
	private OntologyResourceFacade ontologyResourceFacade;

	@Override
	public ArticleInfo load(String uri) {
		ArticleMetadata metadata = ontologyResourceFacade
				.load(new OntologyResource(uri));
		ArticleInfo info = new ArticleInfo();
		info.setUri(metadata.getId());
		info.setTitle(metadata.getTitle());
		List<Author> authors = metadata.getAuthors();
		List<String> authorNames = new ArrayList<String>();
		for (Author author : authors) {
			authorNames.add(author.getName());
		}
		info.setAuthors(authorNames);
		return info;
	}

}
