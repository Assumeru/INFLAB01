package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

public class Shutdown implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws Exception {
		node.shutdown();
	}

	@Override
	public String getAction() {
		return "action";
	}
}
