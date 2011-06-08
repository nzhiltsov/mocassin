package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void testStripLatexMarkup() {
		String str = ".\\label{nef=sa} $\\Pic{(X)}_{\\bq} \\simeq \\Num{(X)}_{\\bq}$ $\bq$-factorial preserving-order \\cite[Proposition 1.11(3)]{this} \\ref{that} (\ref{fin_pic}) the fan of $Y$ coincides with the \textit{restriction} of the fan of $X$ to the subspace $\\Pic{(Y)}_{\\br}\\subset\\Pic{(X)}_{\\br}$: i.e. proof.";
		List<String> strippedTokens = StringUtil.stripLatexMarkup(str);
		String[] expected = {"factorial", "preserving", "order", "the", "fan", "of", "coincides", "with", "the",
				"restriction", "of", "the", "fan", "of", "to", "the",
				"subspace", "ie", "proof" };
		Assert.assertEquals(strippedTokens.size(), expected.length);
		for (int i = 0; i < strippedTokens.size(); i++) {
			Assert.assertEquals(strippedTokens.get(i), expected[i]);
		}
	}
}
