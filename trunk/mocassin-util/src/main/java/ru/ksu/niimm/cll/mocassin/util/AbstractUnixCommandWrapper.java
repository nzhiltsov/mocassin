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

	protected synchronized void setCmdArray(int index, String value) {
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
		ExpectJ expectinator = new ExpectJ(TIMEOUT_IN_SECONDS);

		Spawn shell;
		synchronized (this) {
			String command = StringUtil.asString(cmdArray);
			shell = expectinator.spawn(command);
		}
		shell.expectClose();
		String errOutput = shell.getCurrentStandardErrContents();
		String standardOutput = shell.getCurrentStandardOutContents();

		synchronized (this) {
			boolean isSuccess = true;
			if (successFlag != null) {
				isSuccess = standardOutput.contains(successFlag)
						|| errOutput.contains(successFlag);
			}
			return isSuccess;
		}

	}
}
