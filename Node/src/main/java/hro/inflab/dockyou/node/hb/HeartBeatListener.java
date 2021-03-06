package hro.inflab.dockyou.node.hb;

import java.io.ByteArrayOutputStream;
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
		final Socket socket = serverSocket.accept();
		new Thread(() -> {
			try {
				sendHeartBeat(socket);
			} catch(Exception e) {
				LOG.error("Error sending heart beat", e);
			} finally {
				try {
					socket.close();
				} catch(Exception e) {
					LOG.error("Error closing socket", e);
				}
			}
		}).start();
	}

	private void sendHeartBeat(Socket socket) throws IOException {
		JSONObject output = new JSONObject()
				.put("id", node.getSettings().get("id"))
				.put("containers", node.getContext().getContainers())
				.put("starting", node.getContext().getStartingContainers());
		sendHttp(socket, output.toString());
	}

	private void sendHttp(Socket socket, String message) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] messageBytes = message.getBytes("UTF-8");
		String header = "HTTP/1.1 200 OK\nContent-Type: application/json\nContent-Length: " + messageBytes.length + "\n\n";
		out.write(header.getBytes("UTF-8"));
		out.write(messageBytes);
		socket.getOutputStream().write(out.toByteArray());
		socket.getOutputStream().flush();
	}

	public boolean isRunning() {
		return running;
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
