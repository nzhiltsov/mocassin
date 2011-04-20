package ru.ksu.niimm.cll.mocassin.util;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void testStripLatexMarkup() {
		String str = "the fan of $Y$ coincides with the \textit{restriction} of the fan of $X$ to the subspace $\\Pic{(Y)}_{\\br}\\subset\\Pic{(X)}_{\\br}$: i.e.";
		String stripped = StringUtil.stripLatexMarkup(str);
		String expected = "the fan of  coincides with the restriction of the fan of  to the subspace : i.e.";
		Assert.assertEquals(expected, stripped);
	}
}
