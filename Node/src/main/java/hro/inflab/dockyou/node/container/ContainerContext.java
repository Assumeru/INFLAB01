package hro.inflab.dockyou.node.container;

import org.json.JSONArray;
import org.json.JSONObject;

import hro.inflab.dockyou.node.exception.ContainerException;

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
	 * @throws ContainerException If an error occurs
	 */
	void handle(JSONObject request) throws ContainerException;

	/**
	 * Stops all containers.
	 */
	void stopAll();

	/**
	 * Stops and returns a container
	 * 
	 * @param container
	 * @return An import action
	 * @throws ContainerException 
	 */
	JSONObject export(String container) throws ContainerException;

	/**
	 * @return A list of running containers.
	 */
	JSONArray getContainers();

	/**
	 * @return A list of starting containers.
	 */
	JSONArray getStartingContainers();
}
