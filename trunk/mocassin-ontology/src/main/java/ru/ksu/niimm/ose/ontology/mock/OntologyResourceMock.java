package ru.ksu.niimm.ose.ontology.mock;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;

public class OntologyResourceMock implements OntologyResourceFacade {

	@Override
	public void insert(ArticleMetadata articleMetadata) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

	@Override
	public ArticleMetadata load(OntologyResource resource) {
		ArticleMetadata metadata = new ArticleMetadata();
		metadata.setId(resource.getUri());
		metadata
				.setTitle("Qualitative analysis of the eigenvalue problem for two coupled Ginzburg-Landau equations");
		List<Author> authors = new ArrayList<Author>();
		authors.add(new Author("R. Myrzakulov", ""));
		authors.add(new Author("V. Folomeev", ""));
		authors.add(new Author("V. Dzhunushaliev", ""));
		metadata.setAuthors(authors);
		List<Link> links = new ArrayList<Link>();
		Link pdfLink = new Link();
		pdfLink.setType("application/pdf");
		pdfLink.setHref("http://arxiv.org/pdf/math/0205001v1");
		links.add(pdfLink);
		metadata.setLinks(links);
		return metadata;
	}

	@Override
	public List<ArticleMetadata> loadAll() {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

}
