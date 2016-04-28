package hro.inflab.dockyou.node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import com.rabbitmq.client.Connection;

import hro.inflab.dockyou.node.action.ActionHandler;
import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.hb.HeartBeatListener;

public class Node implements Runnable {
	private static final Logger LOG = LogManager.getLogger();
	private final URL managerUrl;
	private final ContainerContext context;
	private final ActionHandler actionHandler;
	private final Map<String, Object> settings;
	private HeartBeatListener heartBeat;
	private Connection queueConn;

	public Node(URL managerUrl, ContainerContext context) {
		this.managerUrl = managerUrl;
		this.context = context;
		actionHandler = new ActionHandler();
		settings = Collections.synchronizedMap(new HashMap<>());
	}

	@Override
	public void run() {
		try {
			initManager();
			initHeartBeat();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialise", e);
		}
	}

	private void initManager() throws JSONException, IOException, TimeoutException {
		URLConnection conn = managerUrl.openConnection();
		byte[] buffer = new byte[1024];
		try(InputStream input = conn.getInputStream()) {
			conn.connect();
			StringBuilder sb = new StringBuilder();
			int read;
			while((read = input.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, read, "UTF-8"));
			}
			handleActions(new JSONArray(sb.toString()));
		}
	}

	public void handleActions(JSONArray requests) {
		actionHandler.handle(requests, this);
	}

	public ContainerContext getContext() {
		return context;
	}

	private void initHeartBeat() throws IOException {
		heartBeat = new HeartBeatListener(this, 0xD0CC);
		heartBeat.start();
	}

	public Map<String, Object> getSettings() {
		return settings;
	}

	public void shutdown() {
		heartBeat.stop();
		try {
			queueConn.close();
		} catch(IOException e) {
			LOG.error("Error closing connection", e);
		}
		context.stopAll();
	}

	public void setQueue(Connection queueConn) {
		this.queueConn = queueConn;
	}
}
