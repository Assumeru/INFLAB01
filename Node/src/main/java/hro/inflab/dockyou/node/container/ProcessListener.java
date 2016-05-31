package hro.inflab.dockyou.node.container;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hro.inflab.dockyou.node.exception.ProcessException;

public class ProcessListener {
	private static final Logger LOG = LogManager.getLogger();
	private Process process;
	private ExitListener exitListener;

	public ProcessListener(Process process, ExitListener listener) {
		this.process = process;
		this.exitListener = listener;
	}

	public ProcessListener(String command, ExitListener listener) throws IOException {
		this(Runtime.getRuntime().exec(command), listener);
	}

	/**
	 * Starts a thread that waits until the process stops.
	 */
	public void listen() {
		Thread thread = new Thread(new Listener(), "ProcessListener");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Waits for a process to stop, then calls onExit.
	 */
	private class Listener implements Runnable {
		@Override
		public void run() {
			try {
				process.waitFor();
				exitListener.onExit(process, process.exitValue());
			} catch(InterruptedException e) {
				LOG.error("Listener interrupted", e);
				Thread.currentThread().interrupt();
			} catch(Exception e) {
				throw new ProcessException(e);
			}
		}
	}

	@FunctionalInterface
	public static interface ExitListener {
		/**
		 * Called when a process exits.
		 * 
		 * @param process The process that exited
		 * @param exitCode The process' exit code
		 */
		void onExit(Process process, int exitCode) throws ProcessException;
	}
}
