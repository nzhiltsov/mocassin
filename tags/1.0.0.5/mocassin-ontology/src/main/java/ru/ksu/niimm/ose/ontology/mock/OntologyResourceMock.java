package ru.ksu.niimm.ose.ontology.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.ksu.niimm.cll.mocassin.arxiv.ArticleMetadata;
import ru.ksu.niimm.cll.mocassin.arxiv.Author;
import ru.ksu.niimm.cll.mocassin.arxiv.impl.Link;
import ru.ksu.niimm.cll.mocassin.virtuoso.RDFTriple;
import ru.ksu.niimm.ose.ontology.OntologyResource;
import ru.ksu.niimm.ose.ontology.OntologyResourceFacade;
import ru.ksu.niimm.ose.ontology.SGEdge;

public class OntologyResourceMock implements OntologyResourceFacade {

	@Override
	public ArticleMetadata load(OntologyResource resource) {
		ArticleMetadata metadata = new ArticleMetadata();
		metadata.setId(resource.getUri());
		metadata.setTitle("A note on the Gurov-Reshetnyak condition");
		List<Author> authors = new ArrayList<Author>();
		authors.add(new Author("A.A.Korenovskyy", ""));
		authors.add(new Author("A.K.Lerner", ""));
		authors.add(new Author("A.M.Stokolos", ""));
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
	public List<SGEdge> retrieveStructureGraph(OntologyResource resource) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

	@Override
	public List<ArticleMetadata> loadAll() {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

	@Override
	public void insert(ArticleMetadata articleMetadata, Set<RDFTriple> triples) {
		throw new UnsupportedOperationException("this is a mock implementation");
	}

}
