package ru.ksu.niimm.cll.mocassin.util;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.collect.Iterables;

public class StringUtil {
	private static final Pattern STYLE_PATTERN = Pattern
			.compile("\\\\[a-zA-Z]+\\{([^}]*)\\}");
	private static final String REFERENCE_PATTERN_STRING = "\\\\(label|ref|cite){1}(\\[[^]]*\\])*\\{([^}]*)\\}";
	private static final Pattern LATIN_TEXT_PATTERN = Pattern
			.compile("[a-zA-Z-]+");

	private static final String DOLLAR_PATTERN_STRING = "\\$.[^$]*\\$";
	private static final String BACKSLASH_PATTERN = "\\\\[a-z]+";

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
		String name = arxivId.replace("/", "_");
		return String.format("%s.%s", name, extension);
	}
}
