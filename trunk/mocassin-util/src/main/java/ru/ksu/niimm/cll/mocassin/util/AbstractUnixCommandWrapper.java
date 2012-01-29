package ru.ksu.niimm.cll.mocassin.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import expectj.ExpectJ;
import expectj.Spawn;

public abstract class AbstractUnixCommandWrapper {
	private static final long TIMEOUT_IN_SECONDS = 120;
	protected final String[] cmdArray;
	protected final Logger logger;

	public AbstractUnixCommandWrapper(Logger logger, int arraySize) {
		this.cmdArray = new String[arraySize];
		this.logger = logger;
	}

	public final void execute() throws Exception {
		ExpectJ expectinator = new ExpectJ(TIMEOUT_IN_SECONDS);
		Spawn shell = expectinator.spawn(StringUtil.asString(cmdArray));
		shell.expectClose();
	}

}
