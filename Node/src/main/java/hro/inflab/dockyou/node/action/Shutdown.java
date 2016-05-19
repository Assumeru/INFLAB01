package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

/**
 * Shuts the {@link Node} down.
 */
public class Shutdown implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws Exception {
		node.setShutdown(true);
	}

	@Override
	public String getAction() {
		return "shutdown";
	}
}
