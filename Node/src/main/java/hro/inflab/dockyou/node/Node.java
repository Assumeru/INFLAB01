package hro.inflab.dockyou.node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;

import hro.inflab.dockyou.node.action.ActionHandler;
import hro.inflab.dockyou.node.container.ContainerContext;

public class Node implements Runnable {
	private final URL managerUrl;
	private final ContainerContext context;
	private final ActionHandler actionHandler;
	private boolean running;

	public Node(URL managerUrl, ContainerContext context) {
		this.managerUrl = managerUrl;
		this.context = context;
		actionHandler = new ActionHandler();
	}

	@Override
	public void run() {
		running = true;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			running = false;
		}
		while(running) {
			// TODO
		}
	}

	private void init() throws JSONException, IOException, TimeoutException {
		JSONArray actions = registerWithManager();
		handleActions(actions);
	}

	public void handleActions(JSONArray requests) {
		actionHandler.handle(requests, this);
	}

	private JSONArray registerWithManager() throws IOException {
		URLConnection conn = managerUrl.openConnection();
		byte[] buffer = new byte[1024];
		try(InputStream input = conn.getInputStream()) {
			conn.connect();
			StringBuilder sb = new StringBuilder();
			int read;
			while((read = input.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, read, "UTF-8"));
			}
			return new JSONArray(sb.toString());
		}
	}
}
