package hro.inflab.dockyou.node.action;

import org.json.JSONObject;

import hro.inflab.dockyou.node.Node;
import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.exception.ActionException;
import hro.inflab.dockyou.node.exception.ContainerException;

/**
 * Handles a container action using the {@link Node}'s {@link ContainerContext}.
 */
public class ContainerTask implements Action {
	@Override
	public void handle(JSONObject request, Node node) throws ActionException {
		try {
			node.getContext().handle(request);
		} catch (ContainerException e) {
			throw new ActionException("Failed to handle container action", e);
		}
	}

	@Override
	public String getAction() {
		return "container";
	}
}
