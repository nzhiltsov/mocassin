package ru.ksu.niimm.cll.mocassin.util;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

public abstract class AbstractUnixCommandWrapper {
	private static final long TIMEOUT_IN_SECONDS = 120;
	protected final String[] cmdArray;
	protected final Logger logger;

	public AbstractUnixCommandWrapper(Logger logger, int arraySize) {
		this.cmdArray = new String[arraySize];
		this.logger = logger;
	}

	public void execute() throws Exception {
		Process process = Runtime.getRuntime().exec(cmdArray);
		long now = System.currentTimeMillis();
		long timeoutInMillis = 1000L * TIMEOUT_IN_SECONDS;
		long finish = now + timeoutInMillis;
		while (isAlive(process) && (System.currentTimeMillis() < finish)) {
			Thread.sleep(10);
		}
		if (isAlive(process)) {
			throw new InterruptedException("Process timeout out after "
					+ TIMEOUT_IN_SECONDS + " seconds");
		}

		if (process.exitValue() == 0) {
			return;
		} else {
			InputStream errorStream = process.getErrorStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(errorStream, writer, "utf8");
			logger.log(Level.SEVERE, writer.toString());
			throw new Exception("process termination hasn't been normal");
		}
	}

	public static boolean isAlive(Process p) {
		try {
			p.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

}
