package ru.ksu.niimm.cll.mocassin.util;

import expectj.ExpectJ;
import expectj.Spawn;

public abstract class AbstractUnixCommandWrapper {
	private static final long TIMEOUT_IN_SECONDS = 120;
	protected final String[] cmdArray;
	private final String successFlag;

	public AbstractUnixCommandWrapper(int arraySize, String successFlag) {
		this.cmdArray = new String[arraySize];
		this.successFlag = successFlag;
	}

	public AbstractUnixCommandWrapper(int arraySize) {
		this.cmdArray = new String[arraySize];
		this.successFlag = null;
	}

	/**
	 * 
	 * @return true, if execution was successful
	 * @throws Exception
	 */
	public final boolean execute() throws Exception {
		ExpectJ expectinator = new ExpectJ(TIMEOUT_IN_SECONDS);

		Spawn shell = expectinator.spawn(StringUtil.asString(cmdArray));
		shell.expectClose();
		String errOutput = shell.getCurrentStandardErrContents();
		String standardOutput = shell.getCurrentStandardOutContents();
		if (successFlag != null) {
			boolean contains = standardOutput.contains(successFlag)
					|| errOutput.contains(successFlag);
			return contains;
		}
		return true;
	}
}
