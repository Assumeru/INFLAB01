package hro.inflab.dockyou.node.action;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reflections.Reflections;

import hro.inflab.dockyou.node.Node;
import hro.inflab.dockyou.node.exception.ActionException;
import hro.inflab.dockyou.node.exception.ExceptionsException;

public class ActionHandler {
	private static final Logger LOG = LogManager.getLogger();
	private static final Map<String, Action> actions = new HashMap<>();

	public ActionHandler() {
		if(actions.isEmpty()) {
			init();
		}
	}

	/**
	 * Builds a map of classes implementing {@link Action}.
	 */
	private static void init() {
		Set<Class<? extends Action>> actions = new Reflections(Action.class.getPackage().getName())
				.getSubTypesOf(Action.class);
		for(Class<? extends Action> actionClass : actions) {
			if(Modifier.isAbstract(actionClass.getModifiers()) || actionClass.isInterface()) {
				continue;
			}
			try {
				Action action = actionClass.newInstance();
				ActionHandler.actions.put(action.getAction(), action);
			} catch(Exception e) {
				LOG.error("Failed to init " + actionClass, e);
			}
		}
	}

	/**
	 * Finds an {@link Action} matching the request and executes it.
	 * 
	 * @param request The request to handle
	 * @param node The node to handle the request on
	 * @throws ActionException
	 */
	private void handle(JSONObject request, Node node) throws ActionException {
		Action action;
		try {
			String actionName = request.getString("action");
			action = actions.get(actionName);
			if(action == null) {
				throw new ActionException("Unknown action " + actionName);
			}
		} catch(JSONException e) {
			throw new ActionException("Invalid action", e);
		}
		action.handle(request, node);
	}

	/**
	 * Handles an array of requests.
	 * 
	 * @param requests The requests to handle
	 * @param node The node to use
	 */
	public void handle(JSONArray requests, Node node) {
		ExceptionsException exception = new ExceptionsException();
		for(Object request : requests) {
			try {
				handle((JSONObject) request, node);
			} catch(Exception e) {
				exception.add(e);
			}
		}
		if(!exception.isEmpty()) {
			throw exception;
		}
	}
}
