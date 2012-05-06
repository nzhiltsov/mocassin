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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class IOUtilTest {
	private InputStream inputStream;

	@Before
	public void init() {
		inputStream = getClass().getResourceAsStream("/test_list_ids.txt");
	}

	@Test
	public void testReadLineList() throws IOException {
		LinkedList<String> list = IOUtil.readLineList(inputStream);
		Assert.assertEquals(
				"Number of read elements does not equal to the expected one.",
				3, list.size());
		Iterator<String> iterator = list.iterator();
		Assert.assertEquals("1st expected id is different", "ivm1",
				iterator.next());
		Assert.assertEquals("2d expected id is different", "ivm2",
				iterator.next());
		Assert.assertEquals("3d expected id is different", "ivm3",
				iterator.next());
	}
}
