package hro.inflab01.node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Node implements Runnable {
	private final URL managerUrl;
	private final Context context;
	private boolean running;

	public Node(URL managerUrl, Context context) {
		this.managerUrl = managerUrl;
		this.context = context;
	}

	@Override
	public void run() {
		running = true;
		registerWithManager();
		while(running) {
			//TODO
		}
	}

	private void registerWithManager() {
		try {
			URLConnection conn = managerUrl.openConnection();
			try(InputStream input = conn.getInputStream()) {
				conn.connect();
				//TODO something
			}
		} catch (IOException e) {
			running = false;
			System.err.println("Failed to register with manager");
		}
	}
}
