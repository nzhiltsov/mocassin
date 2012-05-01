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

    /**
     * 
     * @return true, if execution was successful
     * @throws Exception
     */
    public final boolean execute(String[] cmdArray, String successFlag)
	    throws Exception {

	String command = StringUtil.asString(cmdArray);
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
