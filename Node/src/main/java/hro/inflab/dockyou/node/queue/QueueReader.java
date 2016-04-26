package hro.inflab.dockyou.node.queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import hro.inflab.dockyou.node.Node;

public class QueueReader extends DefaultConsumer {
	private static final Logger LOG = LogManager.getLogger();
	private final Node node;

	public QueueReader(Channel channel, Node node) {
		super(channel);
		this.node = node;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
		LOG.trace("Received message from queue. " + consumerTag + " " + body.length);
		try {
			JSONArray requests = new JSONArray(new String(body, "UTF-8"));
			node.handleActions(requests);
			getChannel().basicAck(envelope.getDeliveryTag(), false);
		} catch(Exception e) {
			LOG.error("Failed to handle message", e);
		}
	}
}
