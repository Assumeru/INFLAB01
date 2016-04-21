package hro.inflab.dockyou.node.queue;

import java.io.IOException;

import org.json.JSONArray;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import hro.inflab.dockyou.node.Node;

public class QueueReader extends DefaultConsumer {
	private final Node node;

	public QueueReader(Channel channel, Node node) {
		super(channel);
		this.node = node;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) throws IOException {
		JSONArray requests = new JSONArray(new String(body, "UTF-8"));
		node.handleActions(requests);
	}
}
