package ru.ksu.niimm.cll.mocassin.fulltext.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;

import ru.ksu.niimm.cll.mocassin.fulltext.EmptyResultException;
import ru.ksu.niimm.cll.mocassin.fulltext.PDFIndexer;
import ru.ksu.niimm.cll.mocassin.fulltext.PersistingDocumentException;
import ru.ksu.niimm.cll.mocassin.fulltext.providers.IndexSearcherProvider;
import ru.ksu.niimm.cll.mocassin.fulltext.providers.IndexWriterProvider;

import com.google.inject.Inject;

public class PDFLuceneIndexer implements PDFIndexer {
	@Inject
	private Logger logger;
	@Inject
	private IndexWriterProvider<IndexWriter> indexWriterProvider;
	@Inject
	private IndexSearcherProvider<IndexSearcher> indexSearcherProvider;

	@Override
	public void save(String pdfDocumentUri, InputStream pdfInputStream)
			throws PersistingDocumentException {
		try {
			PDDocument pdfDoc = PDDocument.load(pdfInputStream);
			Splitter splitter = new Splitter();
			List<PDDocument> docs = splitter.split(pdfDoc);
			for (int i = 0; i < docs.size(); i++) {
				PDDocument doc = docs.get(i);
				PDFTextStripper textStripper = new PDFTextStripper();
				String text = textStripper.getText(doc);
				Document luceneDoc = new Document();
				luceneDoc.add(new Field("filename", generateLuceneDocUri(
						pdfDocumentUri, i + 1), Store.YES, Index.NOT_ANALYZED));
				luceneDoc.add(new Field("numberpage", String.valueOf(i + 1),
						Store.YES, Index.NOT_ANALYZED, TermVector.NO));
				luceneDoc.add(new Field("contents", text, Store.NO,
						Index.ANALYZED_NO_NORMS,
						TermVector.WITH_POSITIONS_OFFSETS));
				indexWriterProvider.get().addDocument(luceneDoc);
				doc.close();
			}
			indexWriterProvider.get().commit();
			pdfDoc.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, String.format(
					"failed to save a PDF document %s in the index due to: %s",
					pdfDocumentUri, e.getMessage()));
			throw new PersistingDocumentException(e);
		}

	}

	@Override
	public int getPageNumber(String pdfDocumentUri, String fullTextQuery)
			throws EmptyResultException {
		QueryParser queryParser = new QueryParser("contents",
				new StandardAnalyzer());
		int pageNumber = -1;
		try {
			Query query = queryParser.parse(fullTextQuery);
			TopDocs docs = indexSearcherProvider.get().search(query, 1);
			if (docs.scoreDocs.length == 1) {
				Document foundDoc = indexSearcherProvider.get().doc(
						docs.scoreDocs[0].doc);
				pageNumber = Integer.parseInt(foundDoc.get("numberpage"));
			}

		} catch (ParseException e) {
			logger.log(Level.SEVERE, "failed to parse a given query: "
					+ fullTextQuery);
			throw new EmptyResultException(e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format(
					"failed to search with a given query '%s' due to: %s",
					fullTextQuery, e.getMessage()));
			throw new EmptyResultException(e);
		}

		if (pageNumber != -1) {
			logger.log(Level.INFO, String.format("search '%s' is OK",
					fullTextQuery));
			return pageNumber;
		}
		throw new EmptyResultException("page number was not found");
	}

	/**
	 * URI for a Lucene document is generated using given PDF URI and the number
	 * of the corresponding page
	 * 
	 * @param pdfDocumentUri
	 * @param i
	 * @return
	 */
	private String generateLuceneDocUri(String pdfDocumentUri, int i) {
		return String.format("%s/%d", pdfDocumentUri, i);
	}
}
