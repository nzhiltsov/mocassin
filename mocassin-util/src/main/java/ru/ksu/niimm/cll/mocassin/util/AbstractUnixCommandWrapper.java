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

import expectj.ExpectJ;
import expectj.Spawn;

public abstract class AbstractUnixCommandWrapper {
	private static final long TIMEOUT_IN_SECONDS = 180;
	private final String[] cmdArray;
	private final String successFlag;

	public AbstractUnixCommandWrapper(int arraySize, String successFlag) {
		this.cmdArray = new String[arraySize];
		this.successFlag = successFlag;
	}

	public AbstractUnixCommandWrapper(int arraySize) {
		this.cmdArray = new String[arraySize];
		this.successFlag = null;
	}

	protected synchronized final void setCmdArray(int index, String value) {
		if (index < 0 || index >= this.cmdArray.length)
			throw new ArrayIndexOutOfBoundsException(index);
		this.cmdArray[index] = value;
	}

	/**
	 * 
	 * @return true, if execution was successful
	 * @throws Exception
	 */
	public final boolean execute() throws Exception {

		String command = null;
		synchronized (this) {
			command = StringUtil.asString(cmdArray);
		}
		ExpectJ expectinator = new ExpectJ(TIMEOUT_IN_SECONDS);
		Spawn shell = expectinator.spawn(command);
		shell.expectClose();
		String errOutput = shell.getCurrentStandardErrContents();
		String standardOutput = shell.getCurrentStandardOutContents();

		boolean isSuccess = true;
		if (successFlag != null) {
			isSuccess = standardOutput.contains(successFlag)
					|| errOutput.contains(successFlag);
		}
		return isSuccess;

	}
}
