package hro.inflab.dockyou.node;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessListener {
	private static final Logger LOG = LogManager.getLogger();
	private Process process;
	private ExitListener exitListener;

	public ProcessListener(Process process, ExitListener listener) {
		this.process = process;
		this.exitListener = listener;
		Thread thread = new Thread(new Listener(), "ProcessListener");
		thread.setDaemon(true);
		thread.start();
	}

	public ProcessListener(String command, ExitListener listener) throws IOException {
		this(Runtime.getRuntime().exec(command), listener);
	}

	private class Listener implements Runnable {
		@Override
		public void run() {
			try {
				process.waitFor();
				exitListener.onExit(process, process.exitValue());
			} catch(InterruptedException e) {
				LOG.error("Listener interrupted", e);
			}
		}
	}

	@FunctionalInterface
	public static interface ExitListener {
		void onExit(Process process, int exitCode);
	}
}
