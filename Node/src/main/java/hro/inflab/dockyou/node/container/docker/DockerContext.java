package hro.inflab.dockyou.node.container.docker;

import org.json.JSONObject;

import hro.inflab.dockyou.node.container.ContainerContext;

public class DockerContext implements ContainerContext {
	@Override
	public void handle(JSONObject request) throws Exception {
		JSONObject args = request.getJSONObject("docker");
		Runtime.getRuntime().exec("docker " + parseCommand(args));
	}

	private String parseCommand(JSONObject request) {
		//TODO
		return "stats";
	}
}
