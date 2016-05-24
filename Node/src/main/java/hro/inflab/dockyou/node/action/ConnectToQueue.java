package hro.inflab.dockyou.node.action;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import hro.inflab.dockyou.node.Node;
import hro.inflab.dockyou.node.queue.QueueReader;

/**
 * Creates a connection to the queue and saves it in the {@link Node}.
 */
public class ConnectToQueue implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws Exception {
		JSONObject config = request.getJSONObject("amqp");
		Connection conn = createConnection(config);
		node.setQueue(conn);
		Channel channel = conn.createChannel();
		channel.basicConsume(config.getString("queue"), new QueueReader(channel, node));
	}

	@Override
	public String getAction() {
		return "connect";
	}

	static Connection createConnection(JSONObject config) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		factory.setHost(config.getString("host"));
		factory.setVirtualHost(config.getString("virtualhost"));
		factory.setPort(config.getInt("port"));
		factory.setUsername(config.getString("username"));
		factory.setPassword(config.getString("password"));
		return factory.newConnection();
	}
}
