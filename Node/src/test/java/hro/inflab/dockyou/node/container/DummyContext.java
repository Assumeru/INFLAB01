package hro.inflab.dockyou.node.container;

import org.json.JSONArray;
import org.json.JSONObject;

public class DummyContext implements ContainerContext {
	private JSONArray containers = new JSONArray();

	@Override
	public void handle(JSONObject request) throws Exception {
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
	public JSONObject export(String container) throws Exception {
		return null;
	}

	@Override
	public JSONArray getContainers() {
		return containers;
	}
}
