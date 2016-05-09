package hro.inflab.dockyou.node.container.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import hro.inflab.dockyou.node.ProcessListener;
import hro.inflab.dockyou.node.ProcessListener.ExitListener;
import hro.inflab.dockyou.node.container.ContainerContext;

public class DockerContext implements ContainerContext {
	private static final Logger LOG = LogManager.getLogger();
	private static final ExitListener DEFAULT_EXIT_LISTENER = new ExitListener() {
		@Override
		public void onExit(Process process, int exitCode) {
			LOG.info("Process terminated with exit code " + exitCode);
			LOG.info(copyToString(process.getInputStream()));
			LOG.info(copyToString(process.getErrorStream()));
		}
	};

	@Override
	public void handle(JSONObject request) throws Exception {
		JSONObject args = request.getJSONObject("docker");
		runCommand(parseCommand(args));
	}

	private void runCommand(String command) throws IOException {
		runCommand(command, DEFAULT_EXIT_LISTENER);
	}

	private void runCommand(String command, ExitListener exitListener) throws IOException {
		LOG.info(command);
		new ProcessListener(command, exitListener);
	}

	private static String copyToString(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read;
		try {
			while((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
		} catch(IOException e) {
			LOG.error("Failed to read from stream", e);
		}
		return out.toString();
	}

	private String parseCommand(JSONObject request) {
		//TODO delete
		if(request.has("test")) {
			return request.getString("test");
		}
		if(request.has("pull")) {
			return "docker pull " + request.get("pull");
		} else if(request.has("run")) {
			return parseRun(request.getJSONObject("run"));
		}
		//TODO
		return "docker stats";
	}

	/**
	 * <pre>
	 * {
	 * 	"action": "container",
	 * 	"docker": {
	 * 		"run": {
	 * 			"name": [name],
	 * 			"environment": {
	 * 				[key]: [value]
	 * 			},
	 * 			"image": [image],
	 * 			"detached": [true|false]
	 * 		}
	 * 	}
	 * }
	 */
	private String parseRun(JSONObject args) {
		StringBuilder cmd = new StringBuilder("docker run");
		if(args.has("name")) {
			cmd.append(" --name ").append(args.get("name"));
		}
		if(!args.has("detached") || args.getBoolean("detached")) {
			cmd.append(" -d");
		}
		if(args.has("environment")) {
			JSONObject envVars = args.getJSONObject("environment");
			for(String key : envVars.keySet()) {
				cmd.append(" -e \"").append(key).append('=').append(envVars.get(key)).append('"');
			}
		}
		cmd.append(' ').append(args.get("image"));
		return cmd.toString();
	}

	@Override
	public void stopAll() {
		try {
			runCommand("docker ps -a -q", (process, exitCode) -> {
				if(exitCode != 0) {
					DEFAULT_EXIT_LISTENER.onExit(process, exitCode);
				} else {
					runCommand("docker stop " + String.join(" ", copyToString(process.getInputStream()).split("\n")));
				}
			});
		} catch(Exception e) {
			LOG.error("Failed to stop all containers", e);
		}
	}
}
