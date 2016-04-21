package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;

public interface Action {
	void handle(JSONObject request, Node node);

	String getAction();
}
