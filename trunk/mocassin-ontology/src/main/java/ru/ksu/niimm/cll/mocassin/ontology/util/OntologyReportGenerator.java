package ru.ksu.niimm.cll.mocassin.ontology.util;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.AllValuesFromRestriction;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public final class OntologyReportGenerator {
	private static final String OWL_THING_URI = "http://www.w3.org/2002/07/owl#Thing";
	private static final Pattern SMALL_RUSSIAN_START_LETTER_PATTERN = Pattern
			.compile("^[а-я].*");
	private static final String RU_LOCALE = "ru";
	private static final String EN_LOCALE = "en";

	private static final Map<String, String> propertyUri2code = Maps
			.newHashMap();
	private static final Map<String, String> classUri2code = Maps
			.newLinkedHashMap();

	private OntologyReportGenerator() {
	}

	public static XWPFDocument generate(OntModel model) {
		initPropertyCodes(model);
		initClassCodes(model);
		XWPFDocument wordDocument = new XWPFDocument();
		Set<String> keys = classUri2code.keySet();
		for (String ontClassUri : keys) {
			OntClass ontClass = model.getOntClass(ontClassUri);
			String rdfsLabel = ontClass.getLabel(RU_LOCALE) != null ? ontClass
					.getLabel(RU_LOCALE) : ontClass.getLabel(EN_LOCALE);
			rdfsLabel = rdfsLabel != null ? rdfsLabel : ontClass.getURI()
					.substring(ontClass.getURI().indexOf("#") + 1);
			String comment = ontClass.getComment(RU_LOCALE);
			addURI(wordDocument, ontClass);
			addSuperclasses(wordDocument, ontClass);
			addSubclasses(wordDocument, ontClass);
			addComment(wordDocument, comment);
			wordDocument.createParagraph().setSpacingAfter(10);
			addProperties(wordDocument, ontClass);
		}
		return wordDocument;
	}

	private static void initClassCodes(OntModel model) {
		int i = 1;
		Stack<OntClass> stack = new Stack<OntClass>();
		stack.push(model.getOntClass(OWL_THING_URI));
		while (!stack.isEmpty()) {
			OntClass clazz = stack.pop();
			if (!clazz.getURI().equals(OWL_THING_URI)
					&& !classUri2code.containsKey(clazz.getURI())) {
				classUri2code.put(clazz.getURI(), format("E%d", i));
				i++;
			}
			List<OntClass> children = clazz.listSubClasses(true).toList();
			Collections.sort(children, new OntClassByLabelComparatorDesc());
			for (OntClass child : children) {
				stack.push(child);
			}
		}
	}

	private static void initPropertyCodes(OntModel model) {
		ExtendedIterator<ObjectProperty> propIt = model.listObjectProperties();
		int i = 1;
		while (propIt.hasNext()) {
			ObjectProperty property = propIt.next();
			propertyUri2code.put(property.getURI(), format("P%d", i));
			i++;
		}
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
		return format("%s %s", propertyUri2code.get(onProperty.getURI()),
				onProperty.getLabel(RU_LOCALE).replace("_", " "));
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
		String preparedLabel = prepareLabel(ontClass);
		if (ontClass.getURI().equals(OWL_THING_URI))
			return format("owl:%s", preparedLabel);
		return format("%s %s", classUri2code.get(ontClass.getURI()),
				preparedLabel);

	}

	private static String prepareLabel(OntClass ontClass) {
		String rdfsLabel = ontClass.getLabel(RU_LOCALE) != null ? ontClass
				.getLabel(RU_LOCALE) : ontClass.getLabel(EN_LOCALE);
		rdfsLabel = rdfsLabel != null ? rdfsLabel : ontClass.getURI()
				.substring(ontClass.getURI().indexOf("#") + 1);
		if (SMALL_RUSSIAN_START_LETTER_PATTERN.matcher(rdfsLabel).matches()) {
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
}
