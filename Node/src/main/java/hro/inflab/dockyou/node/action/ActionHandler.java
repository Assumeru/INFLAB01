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

import hro.inflab.dockyou.node.ExceptionsException;
import hro.inflab.dockyou.node.Node;

public class ActionHandler {
	private static final Logger LOG = LogManager.getLogger();
	private static final Map<String, Action> actions = new HashMap<>();

	public ActionHandler() {
		if(actions.isEmpty()) {
			init();
		}
	}

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

	public void handle(JSONObject request, Node node) {
		Action action;
		try {
			String actionName = request.getString("action");
			action = actions.get(actionName);
			if(action == null) {
				throw new RuntimeException("Unknown action " + actionName);
			}
		} catch(JSONException e) {
			throw new RuntimeException("Invalid action", e);
		}
		action.handle(request, node);
	}

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
