package hro.inflab.dockyou.node.hb;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

/**
 * Listens on a {@link Socket} and returns the {@link Node}'s id.
 */
public class HeartBeatListener implements Runnable {
	private static final Logger LOG = LogManager.getLogger();
	private Node node;
	private int port;
	private boolean running;
	private ServerSocket serverSocket;

	public HeartBeatListener(Node node, int port) {
		this.port = port;
		this.node = node;
	}

	@Override
	public void run() {
		if(!running) {
			throw new IllegalStateException("Not running");
		}
		try {
			while(running) {
				listen();
			}
		} catch (IOException e) {
			if(running) {
				LOG.error("Error listening for connections", e);
			}
		} finally {
			tryClose();
		}
	}

	private void listen() throws IOException {
		Socket socket = serverSocket.accept();
		try {
			sendHeartBeat(socket);
		} catch(IOException e) {
			LOG.error("Error sending heart beat", e);
		} finally {
			socket.close();
		}
	}

	private void sendHeartBeat(Socket socket) throws IOException {
		JSONObject output = new JSONObject()
				.put("id", node.getSettings().get("id"))
				.put("containers", node.getContext().getContainers());
		socket.getOutputStream().write(output.toString().getBytes("UTF-8"));
		socket.getOutputStream().flush();
	}

	private void tryClose() {
		running = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			LOG.error("Failed to close socket", e);
		}
	}

	public void stop() {
		tryClose();
	}

	public Thread start() throws IOException {
		if(running) {
			throw new IllegalStateException("Already running");
		}
		running = true;
		serverSocket = new ServerSocket(port);
		Thread thread = new Thread(this);
		thread.start();
		return thread;
	}
}
