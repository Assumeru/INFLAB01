package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;
import hro.inflab.dockyou.node.exception.ActionException;

public interface Action {
	/**
	 * Handles an action.
	 * 
	 * @param request The request to handle
	 * @param node The node to handle it on
	 * @throws ActionException If something goes wrong
	 */
	void handle(JSONObject request, Node node) throws ActionException;

	/**
	 * @return A tag to match the action on.
	 */
	String getAction();
}
