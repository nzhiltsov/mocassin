package ru.ksu.niimm.cll.mocassin.parser.latex.builder.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import ru.ksu.niimm.cll.mocassin.parser.Node;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardMathEnvironments;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardMetadataEnvironments;
import ru.ksu.niimm.cll.mocassin.parser.util.StandardStyleEnvironments;

public class NumberingProcessorImpl implements NumberingProcessor {

	private static final String THEOREM_LIKE_ENVIRONMENT_NUMBERING_PATTERN = "%s %d.%d";
	private static final String SUBSECTION_NUMBERING_PATTERN = "%d.%d. %s";
	private static final String SUBSECTION_ENVIRONMENT_NAME = "subsection";
	private static final String SECTION_ENVIRONMENT_NAME = "section";
	private static final String DOTTED_SECTION_NUMBERING_PATTERN = "%d. %s";
	private static final String SECTION_NUMBERING_PATTERN = "%d %s";
	private static final String ABSTRACT_ENVIRONMENT_NAME = "abstract";
	private static final String DOCUMENT_ENVIRONMENT_NAME = "document";
	private static final String PROOF_ENVIRONMENT_NAME = "proof";
	private static final String SUBSUBSECTION_NUMBERING_PATTERN = "%d.%d.%d %s";

	@Override
	public void processConsecutiveNumbers(SortedSet<Node> sortedNodes) {
		Multimap<String, Node> multimap = LinkedHashMultimap.create();
		for (Node node : sortedNodes) {
			if (shouldSkip(node))
				continue;
			multimap.put(node.getName(), node);
		}
		for (String key : multimap.keySet()) {
			Collection<Node> environments = multimap.get(key);
			Iterator<Node> it = environments.iterator();
			boolean isSection = key.equals(SECTION_ENVIRONMENT_NAME);
			int i = 1;
			while (it.hasNext()) {
				Node env = it.next();
				String title = isSection ? String.format(
						SECTION_NUMBERING_PATTERN, i, env.getTitle()) : String
						.format("%s %d", env.getTitle(), i);
				env.setTitle(title);
				i++;
			}
		}
	}

	@Override
	public void processWithinSectionNumbers(SortedSet<Node> sortedNodes) {

		int currentSectionNumber = 0;
		int currentEnvironmentNumber = 0;
		int currentSubsectionNumber = 0;
		int currentSubSubSectionNumber = 0;
		for (Node node : sortedNodes) {
			String nodeName = node.getName();
			if (shouldSkip(node))
				continue;
			if (nodeName.equals(SECTION_ENVIRONMENT_NAME)) {
				currentSectionNumber++;
				node.setTitle(String.format(DOTTED_SECTION_NUMBERING_PATTERN,
						currentSectionNumber, node.getTitle()));
				currentEnvironmentNumber = 0;
				currentSubsectionNumber = 0;
				currentSubSubSectionNumber = 0;
			} else if (nodeName.equals(SUBSECTION_ENVIRONMENT_NAME)) {
				currentSubsectionNumber++;
				node.setTitle(String.format(SUBSECTION_NUMBERING_PATTERN,
						currentSectionNumber, currentSubsectionNumber, node
								.getTitle()));
				currentSubSubSectionNumber = 0;
			} else if (nodeName.equals("subsubsection")) {
				currentSubSubSectionNumber++;
				node.setTitle(String.format(SUBSUBSECTION_NUMBERING_PATTERN,
						currentSectionNumber, currentSubsectionNumber,
						currentSubSubSectionNumber, node.getTitle()));
			} else {
				currentEnvironmentNumber++;
				node.setTitle(String.format(
						THEOREM_LIKE_ENVIRONMENT_NUMBERING_PATTERN, node
								.getTitle(), currentSectionNumber,
						currentEnvironmentNumber));
			}
		}
	}

	private static boolean shouldSkip(Node node) {
		String nodeName = node.getName();
		return !node.isNumbered()
				|| StandardMetadataEnvironments.contains(nodeName)
				|| StandardMathEnvironments.contains(nodeName)
				|| StandardStyleEnvironments.contains(nodeName)
				|| nodeName.equals(PROOF_ENVIRONMENT_NAME)
				|| nodeName.equals(DOCUMENT_ENVIRONMENT_NAME)
				|| nodeName.equals(ABSTRACT_ENVIRONMENT_NAME);
	}

}
