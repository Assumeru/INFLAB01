package hro.inflab.dockyou.node.queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import hro.inflab.dockyou.node.Node;

/**
 * Consumes queue messages and lets the {@link Node} handle them.
 *
 * Calls {@link Node#shutdown()} when the connection is broken.
 */
public class QueueReader extends DefaultConsumer {
	private static final Logger LOG = LogManager.getLogger();
	private final Node node;

	public QueueReader(Channel channel, Node node) {
		super(channel);
		this.node = node;
	}

	@Override
	public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
		LOG.trace("Received message from queue. " + consumerTag + " of length " + body.length);
		try {
			JSONArray requests = new JSONArray(new String(body, "UTF-8"));
			node.handleActions(requests);
			getChannel().basicAck(envelope.getDeliveryTag(), false);
		} catch(Exception e) {
			LOG.error("Failed to handle message", e);
		}
		if(node.shouldShutdown()) {
			node.shutdown();
		}
	}

	@Override
	public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
		LOG.warn("Queue connection lost, shutting down...", sig);
		node.shutdown();
	}
}
