package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void testStripLatexMarkup() {
		String str = "\\cite{this} \\ref{that} the fan of $Y$ coincides with the \textit{restriction} of the fan of $X$ to the subspace $\\Pic{(Y)}_{\\br}\\subset\\Pic{(X)}_{\\br}$: i.e.";
		List<String> strippedTokens = StringUtil.stripLatexMarkup(str);
		String[] expected = { "the", "fan", "of", "coincides", "with", "the",
				"restriction", "of", "the", "fan", "of", "to", "the",
				"subspace" };
		Assert.assertEquals(strippedTokens.size(), expected.length);
		for (int i = 0; i < strippedTokens.size(); i++) {
			Assert.assertEquals(strippedTokens.get(i), expected[i]);
		}
	}
}
