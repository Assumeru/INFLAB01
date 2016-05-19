package hro.inflab.dockyou.node.action;

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
		ConnectionFactory factory = new ConnectionFactory();
		factory.setAutomaticRecoveryEnabled(true);
		factory.setHost(config.getString("host"));
		factory.setVirtualHost(config.getString("virtualhost"));
		factory.setPort(config.getInt("port"));
		factory.setUsername(config.getString("username"));
		factory.setPassword(config.getString("password"));
		Connection conn = factory.newConnection();
		node.setQueue(conn);
		Channel channel = conn.createChannel();
		channel.basicConsume(config.getString("queue"), new QueueReader(channel, node));
	}

	@Override
	public String getAction() {
		return "connect";
	}
}
