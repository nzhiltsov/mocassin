package ru.ksu.niimm.cll.mocassin.util;

import expectj.ExpectJ;
import expectj.Spawn;

public abstract class AbstractUnixCommandWrapper {
	private static final long TIMEOUT_IN_SECONDS = 120;
	protected final String[] cmdArray;

	public AbstractUnixCommandWrapper(int arraySize) {
		this.cmdArray = new String[arraySize];
	}

	public final void execute() throws Exception {
		ExpectJ expectinator = new ExpectJ(TIMEOUT_IN_SECONDS);
		Spawn shell = expectinator.spawn(StringUtil.asString(cmdArray));
		shell.expectClose();
	}

}
