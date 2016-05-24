package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import hro.inflab.dockyou.node.Node;

public class Export implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws Exception {
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
