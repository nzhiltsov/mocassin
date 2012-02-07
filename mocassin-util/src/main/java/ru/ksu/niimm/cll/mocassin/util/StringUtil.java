package ru.ksu.niimm.cll.mocassin.util;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Iterables;

public class StringUtil {
	private static final String URI_DELIMITER = "/";
	public static final String ARXIVID_SEGMENTID_DELIMITER = "$";
	private static final Pattern STYLE_PATTERN = Pattern
			.compile("\\\\[a-zA-Z]+\\{([^}]*)\\}");
	private static final String REFERENCE_PATTERN_STRING = "\\\\(label|ref|cite){1}(\\[[^]]*\\])*\\{([^}]*)\\}";
	private static final Pattern LATIN_TEXT_PATTERN = Pattern
			.compile("[a-zA-Z-]+");

	private static final String DOLLAR_PATTERN_STRING = "\\$.[^$]*\\$";
	private static final String BACKSLASH_PATTERN = "\\\\[a-z]+";

	private static final Pattern MATHNET_JOURNAL_PREFIX = Pattern
			.compile("([a-z]+)([0-9]+)");

	private StringUtil() {
	}

	/**
	 * returns a list of tokens (substrings separated by space) for a given
	 * string; if the string is empty or null, this returns empty list
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> tokenize(String str) {
		List<String> tokens = new ArrayList<String>();
		if (str == null || str.equals(""))
			return tokens;
		StringTokenizer st = new StringTokenizer(str, " ");
		while (st.hasMoreTokens()) {
			tokens.add(st.nextToken());
		}
		return tokens;
	}

	/**
	 * Generates an identifier that is unique over time with respect to the host
	 * that it was generated on. Used approach is similar to Jena's.
	 * 
	 * 
	 * @return
	 */
	public static String generateBlankNodeId() {
		return String.format("_:bnode%d", new UID().hashCode());
	}

	/**
	 * remove simple LaTeX constructs e.g. \noindent from a given string
	 * 
	 * @param str
	 * @return
	 */
	public static String takeoutMarkup(String str) {
		return str.replaceAll(BACKSLASH_PATTERN, "").trim();
	}

	/**
	 * returns a list of tokens extracted from a given Latex text preserving
	 * initial order
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> stripLatexMarkup(String str) {
		String escaped = StringEscapeUtils.escapeJava(str)
				.replaceAll(DOLLAR_PATTERN_STRING, "")
				.replaceAll(REFERENCE_PATTERN_STRING, "");

		StringTokenizer st = new StringTokenizer(escaped, "- \t\n\r\f");
		List<String> list = new LinkedList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken().replaceAll(DOLLAR_PATTERN_STRING, "");

			Matcher styleMatcher = STYLE_PATTERN.matcher(token);
			if (styleMatcher.find()) {
				token = styleMatcher.group(1);
			}

			String strippedOffPunctuation = token.replaceAll("\\p{P}+", "");

			if (LATIN_TEXT_PATTERN.matcher(strippedOffPunctuation).matches()) {
				list.add(strippedOffPunctuation);
			}
		}

		return list;
	}

	public static String asString(String[] args) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]);
			if (i < args.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public static String asString(List<String> list) {
		return asString(Iterables.toArray(list, String.class));
	}

	/**
	 * returns a filename that corresponds to a given arXiv paper identifier
	 * 
	 * @param arxivId
	 *            e.g. 'math/0002188'
	 * @param extension
	 *            e.g. 'tex'
	 * @return e.g. 'math_0002188.tex'
	 */
	public static String arxivid2filename(String arxivId, String extension) {
		String name = arxivid2gateid(arxivId);
		return String.format("%s.%s", name, extension);
	}

	public static String segmentid2filename(String arxivId, int segmentId,
			String extension) {
		return String.format("%s%s%d.%s", StringUtil.arxivid2gateid(arxivId),
				ARXIVID_SEGMENTID_DELIMITER, segmentId, extension);
	}

	public static String arxivid2gateid(String arxivId) {
		return arxivId.replace(URI_DELIMITER, "_");
	}

	public static String extractJournalPrefixFromMathnetKey(String mathnetKey) {
		Matcher styleMatcher = MATHNET_JOURNAL_PREFIX.matcher(mathnetKey);
		if (!styleMatcher.matches())
			throw new RuntimeException("the string '" + mathnetKey
					+ "' is not a Mathnet key");
		return styleMatcher.group(1);
	}

	public static String extractPaperNumberFromMathnetKey(String mathnetKey) {
		Matcher styleMatcher = MATHNET_JOURNAL_PREFIX.matcher(mathnetKey);
		if (!styleMatcher.matches())
			throw new RuntimeException("the string '" + mathnetKey
					+ "' is not a Mathnet key");
		return styleMatcher.group(2);
	}

	public static String extractMathnetKeyFromURI(String uri) {
		if (uri == null || uri.length() == 0)
			throw new IllegalArgumentException("URI cannot be null or empty");
		return uri.substring(uri.lastIndexOf(URI_DELIMITER) + 1);
	}

	public static String extractDocumentURIFromSegmentURI(String segmentUri) {
		checkArgument(segmentUri != null
				&& segmentUri.length() > 0,
				"Argument was %s but expected non-empty", segmentUri);
		return segmentUri.substring(0, segmentUri.lastIndexOf(URI_DELIMITER));
	}

}
