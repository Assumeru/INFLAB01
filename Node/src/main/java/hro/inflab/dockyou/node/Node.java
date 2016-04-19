package hro.inflab.dockyou.node;

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
			byte[] buffer = new byte[1024];
			try(InputStream input = conn.getInputStream()) {
				conn.connect();
				StringBuilder sb = new StringBuilder();
				int read;
				while((read = input.read(buffer)) > 0) {
					sb.append(new String(buffer, "UTF-8"));
				}
				System.out.println("output");
				System.out.println(sb.toString());
			}
		} catch (IOException e) {
			running = false;
			System.err.println("Failed to register with manager");
		}
	}
}
