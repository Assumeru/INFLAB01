package hro.inflab.dockyou.node.action;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import hro.inflab.dockyou.node.Node;
import hro.inflab.dockyou.node.exception.ActionException;
import hro.inflab.dockyou.node.exception.ContainerException;

public class Export implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws ActionException {
		try {
			export(request, node);
		} catch(IOException | TimeoutException | ContainerException e) {
			throw new ActionException("Failed to export", e);
		}
	}

	private void export(JSONObject request, Node node) throws IOException, TimeoutException, ContainerException {
		JSONObject config = request.getJSONObject("amqp");
		Connection conn = ConnectToQueue.createConnection(config);
		try {
			Channel channel = conn.createChannel();
			JSONObject importAction = node.getContext().export(request.getString("container"));
			channel.basicPublish("", "", null, importAction.toString().getBytes());
		} finally {
			conn.close();
		}
	}

	@Override
	public String getAction() {
		return "export";
	}
}
