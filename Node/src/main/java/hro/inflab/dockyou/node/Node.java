package hro.inflab.dockyou.node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import hro.inflab.dockyou.node.container.ContainerContext;

public class Node implements Runnable {
	private final URL managerUrl;
	private final ContainerContext context;
	private boolean running;

	public Node(URL managerUrl, ContainerContext context) {
		this.managerUrl = managerUrl;
		this.context = context;
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
		JSONObject config = registerWithManager();
		connectToQueue(config.getJSONObject("amqp"));
	}

	private JSONObject registerWithManager() throws IOException {
		URLConnection conn = managerUrl.openConnection();
		byte[] buffer = new byte[1024];
		try(InputStream input = conn.getInputStream()) {
			conn.connect();
			StringBuilder sb = new StringBuilder();
			int read;
			while((read = input.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, read, "UTF-8"));
			}
			return new JSONObject(sb.toString());
		}
	}

	private void connectToQueue(JSONObject config) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		factory.setHost(config.getString("host"));
		factory.setVirtualHost(config.getString("virtualhost"));
		factory.setPort(config.getInt("port"));
		factory.setUsername(config.getString("username"));
		factory.setPassword(config.getString("password"));
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();
		channel.basicConsume(config.getString("queue"), callback);
	}
}
