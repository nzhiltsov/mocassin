package ru.ksu.niimm.cll.mocassin.ontology.util;

import static java.lang.String.format;

import java.util.List;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public final class OntologyReportGenerator {
	private static final String RU_LOCALE = "ru";

	private OntologyReportGenerator() {
	}

	public static XWPFDocument generate(OntModel model) {
		XWPFDocument wordDocument = new XWPFDocument();
		ExtendedIterator<OntClass> iterator = model.listNamedClasses();
		List<OntClass> iteratorAsList = iterator.toList();
		for (OntClass ontClass : iteratorAsList) {
			String rdfsLabel = ontClass.getLabel(RU_LOCALE);
			String uri = ontClass.getURI();
			String comment = ontClass.getComment(RU_LOCALE);
			addURI(wordDocument, rdfsLabel);
			addSuperclasses(wordDocument, ontClass);
			addComment(wordDocument, comment);
			wordDocument.createParagraph().setSpacingAfter(10);
			addSubclasses(wordDocument, ontClass);
			addProperties(wordDocument, ontClass);
		}
		return wordDocument;
	}

	private static void addProperties(XWPFDocument wordDocument,
			OntClass ontClass) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText("Свойства: ");
		Set<OntProperty> properties = ontClass.listDeclaredProperties(true)
				.toSet();
		for (OntProperty property : properties) {
			XWPFParagraph p = wordDocument.createParagraph();
			p.setIndentationLeft(1440);
			XWPFRun r = p.createRun();
			r.setText(property.getLabel(RU_LOCALE));
			r.setItalic(true);
		}
		wordDocument.createParagraph().setIndentationLeft(1440);
	}

	private static void addComment(XWPFDocument wordDocument, String comment) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText(format("Описание: \t%s", comment != null ? comment : ""));
	}

	private static void addSuperclasses(XWPFDocument wordDocument,
			OntClass ontClass) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText("Суперклассы: ");
		Set<OntClass> superClasses = ontClass.listSuperClasses().toSet();
		for (OntClass clazz : superClasses) {
			XWPFParagraph p = wordDocument.createParagraph();
			p.setIndentationLeft(1440);
			XWPFRun r = p.createRun();
			r.setText(clazz.getLabel(RU_LOCALE));
			r.setItalic(true);
		}
		wordDocument.createParagraph().setIndentationLeft(1440);
	}

	private static void addSubclasses(XWPFDocument wordDocument,
			OntClass ontClass) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText("Подклассы: ");
		Set<OntClass> superClasses = ontClass.listSubClasses().toSet();
		for (OntClass clazz : superClasses) {
			XWPFParagraph p = wordDocument.createParagraph();
			p.setIndentationLeft(1440);
			XWPFRun r = p.createRun();
			r.setText(clazz.getLabel(RU_LOCALE));
			r.setItalic(true);
		}
		wordDocument.createParagraph().setIndentationLeft(1440);
	}

	private static void addURI(XWPFDocument wordDocument, String uri) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setSpacingBefore(10);
		paragraph.setStyle("Heading 5");
		XWPFRun run = paragraph.createRun();
		run.setText(uri);
	}
}
