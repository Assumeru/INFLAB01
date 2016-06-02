package hro.inflab.dockyou.node.container;

import org.json.JSONArray;
import org.json.JSONObject;

public class DummyContext implements ContainerContext {
	private JSONArray containers = new JSONArray();

	@Override
	public void handle(JSONObject request) {
		if(request.has("add")) {
			containers.put(request.get("add"));
		} else if(request.has("remove")) {
			containers.remove(request.getInt("remove"));
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public void stopAll() {
	}

	@Override
	public JSONObject export(String container) {
		return null;
	}

	@Override
	public JSONArray getContainers() {
		return containers;
	}

	@Override
	public JSONArray getStartingContainers() {
		return new JSONArray();
	}
}
