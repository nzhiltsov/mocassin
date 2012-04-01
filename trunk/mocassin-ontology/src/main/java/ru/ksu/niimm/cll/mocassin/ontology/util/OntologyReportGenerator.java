package ru.ksu.niimm.cll.mocassin.ontology.util;

import static java.lang.String.format;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public final class OntologyReportGenerator {
	private static final Pattern SMALL_RUSSIAN_START_LETTER_PATTERN = Pattern
			.compile("^[а-я].*");
	private static final String RU_LOCALE = "ru";
	private static final String EN_LOCALE = "en";

	private OntologyReportGenerator() {
	}

	public static XWPFDocument generate(OntModel model) {
		XWPFDocument wordDocument = new XWPFDocument();
		ExtendedIterator<OntClass> iterator = model.listNamedClasses();
		List<OntClass> iteratorAsList = iterator.toList();
		for (OntClass ontClass : iteratorAsList) {
			String rdfsLabel = ontClass.getLabel(RU_LOCALE) != null ? ontClass
					.getLabel(RU_LOCALE) : ontClass.getLabel(EN_LOCALE);
			rdfsLabel = rdfsLabel != null ? rdfsLabel : ontClass.getURI()
					.substring(ontClass.getURI().indexOf("#") + 1);
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
		run.setText(format("Описание: \t%s",
				comment != null ? comment.replace("http://", "См. http://")
						: ""));
	}

	private static void addNeighbourClasses(XWPFDocument wordDocument,
			Set<OntClass> classes, String header) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText(header);
		StringBuilder sb = new StringBuilder();
		Iterator<OntClass> it = classes.iterator();
		while (it.hasNext()) {
			OntClass clazz = it.next();
			String label = clazz.getLabel(RU_LOCALE) != null ? clazz
					.getLabel(RU_LOCALE) : clazz.getLabel(EN_LOCALE);
			label = label != null ? label : clazz.getURI().substring(
					clazz.getURI().indexOf("#") + 1);
			sb.append(formatURI(label));
			if (it.hasNext()) {
				sb.append("; ");
			}
		}
		XWPFParagraph p = wordDocument.createParagraph();
		p.setIndentationLeft(1440);
		XWPFRun r = p.createRun();
		r.setText(sb.toString());
		r.setItalic(true);
		wordDocument.createParagraph().setIndentationLeft(1440);
	}

	private static void addSubclasses(XWPFDocument wordDocument,
			OntClass ontClass) {
		addNeighbourClasses(wordDocument,
				ontClass.listSubClasses(true).toSet(), "Подклассы: ");
	}

	private static void addSuperclasses(XWPFDocument wordDocument,
			OntClass ontClass) {
		addNeighbourClasses(wordDocument, ontClass.listSuperClasses(true)
				.toSet(), "Суперклассы: ");
	}

	private static void addURI(XWPFDocument wordDocument, String uri) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setSpacingBefore(10);
		paragraph.setStyle("Heading 5");
		XWPFRun run = paragraph.createRun();
		run.setText(formatURI(uri));
	}

	private static String formatURI(String uri) {
		if (SMALL_RUSSIAN_START_LETTER_PATTERN.matcher(uri).matches()) {
			StringBuilder sb = new StringBuilder();
			sb.append(Character.toUpperCase(uri.charAt(0)));
			sb.append(uri.substring(1));
			uri = sb.toString();
		}
		return uri.replace("_", " ");
	}
}
