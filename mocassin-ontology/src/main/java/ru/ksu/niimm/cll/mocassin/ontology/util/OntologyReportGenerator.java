package ru.ksu.niimm.cll.mocassin.ontology.util;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

public final class OntologyReportGenerator {
	private static final String OWL_THING_URI = "http://www.w3.org/2002/07/owl#Thing";
	private static final Pattern SMALL_RUSSIAN_START_LETTER_PATTERN = Pattern
			.compile("^[а-я].*");
	private static final Pattern SMALL_LATIN_START_LETTER_PATTERN = Pattern
			.compile("^[a-z].*");

	private static final String ONTO_MATH_PRO_PREFIX = "http://cll.niimm.ksu.ru/ontologies/mathematics#E";
	private static final String RU_LOCALE = "ru";
	private static final String EN_LOCALE = "en";

	private static final Set<String> classesWithEmptyComments = Sets
			.newHashSet();

	private OntologyReportGenerator() {
	}

	public static OntologyReport generate(OntModel model) {
		XWPFDocument wordDocument = new XWPFDocument();
		List<OntClass> classes = new ArrayList<OntClass>(model
				.listNamedClasses().toList());
		Collections.sort(classes, new OntClassByCodeComparator());
		for (OntClass ontClass : classes) {
			addURI(wordDocument, ontClass);
			addSuperclasses(wordDocument, ontClass);
			addSubclasses(wordDocument, ontClass);
			addComment(wordDocument, ontClass);
			wordDocument.createParagraph().setSpacingAfter(10);
			addProperties(wordDocument, ontClass);
		}
		return new OntologyReport(wordDocument, classesWithEmptyComments);
	}

	private static void addProperties(XWPFDocument wordDocument,
			OntClass ontClass) {
		Set<OntClass> equivalentClasses = ontClass.listEquivalentClasses()
				.toSet();
		if (equivalentClasses.isEmpty())
			return;
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText("Свойства: ");

		for (OntClass equivalentClass : equivalentClasses) {
			if (!equivalentClass.isRestriction())
				continue;
			AllValuesFromRestriction restriction = equivalentClass
					.asRestriction().asAllValuesFromRestriction();
			XWPFParagraph p = wordDocument.createParagraph();
			p.setIndentationLeft(1440);
			XWPFRun r = p.createRun();
			OntProperty onProperty = restriction.getOnProperty();
			OntClass toClass = restriction.getAllValuesFrom()
					.as(OntClass.class);

			r.setText(format("%s: %s", formatPropertyURI(onProperty),
					formatURI(toClass)));
			r.setItalic(true);
		}
		wordDocument.createParagraph().setIndentationLeft(1440);
	}

	private static String formatPropertyURI(OntProperty onProperty) {
		return format("%s %s", onProperty.getLocalName(),
				onProperty.getLabel(RU_LOCALE).replace("_", " "));
	}

	private static void addComment(XWPFDocument wordDocument, OntClass ontClass) {
		String comment = null;
		if (ontClass.getComment(RU_LOCALE) != null) {
			comment = ontClass.getComment(RU_LOCALE);
		} else if (ontClass.getComment(EN_LOCALE) != null) {
			comment = ontClass.getComment(EN_LOCALE);
		} else {
			comment = ontClass.getComment(null);
		}
		if (comment == null || comment.isEmpty()) {
			classesWithEmptyComments.add(formatURI(ontClass));
			return;
		}
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText(format("Описание: \n%s",
				comment != null ? comment.replace("http://", "См. http://")
						.replace("www.", "См. www.") : ""));
	}

	private static void addNeighbourClasses(XWPFDocument wordDocument,
			Set<OntClass> classes, String header) {
		if (classes.isEmpty())
			return;
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setStyle("style0");
		XWPFRun run = paragraph.createRun();
		run.setText(header);
		StringBuilder sb = new StringBuilder();
		List<OntClass> classList = new ArrayList<OntClass>(classes);
		Collections.sort(classList, new OntClassByLabelComparatorAsc());
		Iterator<OntClass> it = classList.iterator();
		while (it.hasNext()) {
			OntClass clazz = it.next();
			sb.append(formatURI(clazz));
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
		Set<OntClass> subclasses = ontClass.listSubClasses(true).toSet();
		if (!subclasses.isEmpty()) {
			addNeighbourClasses(wordDocument, subclasses, "Подклассы: ");
		}
	}

	private static void addSuperclasses(XWPFDocument wordDocument,
			OntClass ontClass) {
		addNeighbourClasses(wordDocument, ontClass.listSuperClasses(true)
				.toSet(), "Суперклассы: ");
	}

	private static void addURI(XWPFDocument wordDocument, OntClass ontClass) {
		XWPFParagraph paragraph = wordDocument.createParagraph();
		paragraph.setSpacingBefore(10);
		paragraph.setStyle("Heading 5");
		XWPFRun run = paragraph.createRun();
		run.setText(formatURI(ontClass));
	}

	private static String formatURI(OntClass ontClass) {

		if (ontClass.getURI().equals(OWL_THING_URI))
			return "owl:Thing";

		if (!ontClass.getURI().startsWith(ONTO_MATH_PRO_PREFIX))
			throw new RuntimeException(format(
					"OntClass='%s' does not have the appropriate URI",
					ontClass.getURI()));
		String preparedLabel = prepareLabel(ontClass);
		return format("%s %s", ontClass.getLocalName(), preparedLabel);

	}

	private static String prepareLabel(OntClass ontClass) {

		String rdfsLabel = null;
		if (ontClass.getLabel(RU_LOCALE) != null) {
			rdfsLabel = ontClass.getLabel(RU_LOCALE);
		} else if (ontClass.getLabel(EN_LOCALE) != null) {
			rdfsLabel = ontClass.getLabel(EN_LOCALE);
		} else {
			rdfsLabel = ontClass.getLabel(null);
		}
		if (rdfsLabel == null)
			throw new RuntimeException(
					"Cannot handle classes with empty labels: "
							+ ontClass.getURI());
		if (SMALL_RUSSIAN_START_LETTER_PATTERN.matcher(rdfsLabel).matches()
				|| SMALL_LATIN_START_LETTER_PATTERN.matcher(rdfsLabel)
						.matches()) {
			StringBuilder sb = new StringBuilder();
			sb.append(Character.toUpperCase(rdfsLabel.charAt(0)));
			sb.append(rdfsLabel.substring(1));
			rdfsLabel = sb.toString();
		}

		String preparedLabel = rdfsLabel.replace("_", " ");
		return preparedLabel;
	}

	private static class OntClassByLabelComparatorDesc implements
			Comparator<OntClass> {

		@Override
		public int compare(OntClass first, OntClass second) {
			return prepareLabel(second).compareTo(prepareLabel(first));
		}

	}

	private static class OntClassByLabelComparatorAsc implements
			Comparator<OntClass> {

		@Override
		public int compare(OntClass first, OntClass second) {
			return prepareLabel(first).compareTo(prepareLabel(second));
		}

	}

	private static class OntClassByCodeComparator implements
			Comparator<OntClass> {

		@Override
		public int compare(OntClass first, OntClass second) {
			int firstCode = Integer.parseInt(first.getLocalName().substring(1));
			int secondCode = Integer.parseInt(second.getLocalName()
					.substring(1));
			if (firstCode < secondCode)
				return -1;
			if (firstCode > secondCode)
				return 1;
			return 0;
		}

	}
}
