package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

public class ContainerTask implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws Exception {
		node.getContext().handle(request);
	}

	@Override
	public String getAction() {
		return "container";
	}
}
