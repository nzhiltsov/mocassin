package ru.ksu.niimm.cll.mocassin.util;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil {
	private StringUtil() {
	}

	/**
	 * returns list of tokens (substrings separated by space) for a given
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

}
