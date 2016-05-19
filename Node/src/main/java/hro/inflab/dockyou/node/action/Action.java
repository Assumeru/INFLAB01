package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

public interface Action {
	/**
	 * Handles an action.
	 * 
	 * @param request The request to handle
	 * @param node The node to handle it on
	 * @throws Exception If something goes wrong
	 */
	void handle(JSONObject request, Node node) throws Exception;

	/**
	 * @return A tag to match the action on.
	 */
	String getAction();
}
