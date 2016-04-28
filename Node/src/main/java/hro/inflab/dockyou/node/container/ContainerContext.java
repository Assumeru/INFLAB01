package hro.inflab.dockyou.node.container;

import org.json.JSONObject;

/**
 * Interface that provides an abstract way of handling implementation-specific requests.
 * 
 * Implementations must declare a default constructor.
 */
public interface ContainerContext {
	void handle(JSONObject request) throws Exception;

	void stopAll();
}
