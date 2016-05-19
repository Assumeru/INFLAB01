package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

/**
 * Sets the {@link Node}'s id.
 */
public class SetId implements Action {
	@Override
	public void handle(JSONObject request, Node node) {
		node.getSettings().put("id", request.getInt("id"));
	}

	@Override
	public String getAction() {
		return "set-id";
	}
}
