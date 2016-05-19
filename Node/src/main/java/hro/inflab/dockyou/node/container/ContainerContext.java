package hro.inflab.dockyou.node.container;

import org.json.JSONObject;

/**
 * Interface that provides an abstract way of handling implementation-specific requests.
 * 
 * Implementations must declare a default constructor.
 */
public interface ContainerContext {
	/**
	 * Handles a request.
	 * 
	 * @param request The request to handle
	 * @throws Exception If an error occurs
	 */
	void handle(JSONObject request) throws Exception;

	/**
	 * Stops all containers.
	 */
	void stopAll();
}
