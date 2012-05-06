/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class StringUtilTest {
	@Test
	public void testStripLatexMarkup() {
		String str = ".\\label{nef=sa} $\\Pic{(X)}_{\\bq} \\simeq \\Num{(X)}_{\\bq}$ $\bq$-factorial preserving-order \\cite[Proposition 1.11(3)]{this} \\ref{that} (\ref{fin_pic}) the fan of $Y$ coincides with the \textit{restriction} of the fan of $X$ to the subspace $\\Pic{(Y)}_{\\br}\\subset\\Pic{(X)}_{\\br}$: i.e. proof.";
		List<String> strippedTokens = StringUtil.stripLatexMarkup(str);
		String[] expected = { "factorial", "preserving", "order", "the", "fan",
				"of", "coincides", "with", "the", "restriction", "of", "the",
				"fan", "of", "to", "the", "subspace", "ie", "proof" };
		Assert.assertEquals(strippedTokens.size(), expected.length);
		for (int i = 0; i < strippedTokens.size(); i++) {
			Assert.assertEquals(strippedTokens.get(i), expected[i]);
		}
	}

	@Test
	public void testExtractDocumentURIFromSegmentURI() {
		String docUri = StringUtil
				.extractDocumentURIFromSegmentURI("http://mathnet.ru/ivm537/1017");
		Assert.assertEquals(
				"Extracted document URI does not equal to the expected one",
				"http://mathnet.ru/ivm537", docUri);
	}

	@Test
	public void testGetFullTextQuery() {
		Assert.assertEquals("group", StringUtil.getFullTextQuery("group"));
		Assert.assertEquals("finite AND group", StringUtil.getFullTextQuery("finite group"));
		Assert.assertEquals("finite AND group", StringUtil.getFullTextQuery("finite and group"));
		Assert.assertEquals("group OR ring", StringUtil.getFullTextQuery("group or ring"));
		Assert.assertEquals("'группа'", StringUtil.getFullTextQuery("группа"));
		Assert.assertEquals("'нильпотентная' AND 'разложимая' AND 'группа'", StringUtil.getFullTextQuery("нильпотентная разложимая группа"));
		Assert.assertEquals("'группа' OR 'кольцо'", StringUtil.getFullTextQuery("группа or кольцо"));
	}
}
