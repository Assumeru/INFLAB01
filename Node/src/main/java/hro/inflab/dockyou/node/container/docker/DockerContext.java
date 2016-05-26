package hro.inflab.dockyou.node.container.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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

	/**
	 * Runs a command using the default {@link ExitListener}.
	 * 
	 * @param command The command to run
	 * @throws IOException
	 */
	private void runCommand(String command) throws IOException {
		if(command != null) {
			runCommand(command, DEFAULT_EXIT_LISTENER);
		}
	}

	/**
	 * Runs a command with the given {@link ExitListener}.
	 * 
	 * @param command The command to run
	 * @param exitListener The {@link ExitListener} to use
	 * @throws IOException
	 */
	private void runCommand(String command, ExitListener exitListener) throws IOException {
		LOG.info(command);
		new ProcessListener(command, exitListener);
	}

	/**
	 * Copies an {@link InputStream} to a {@link String}
	 * 
	 * @param in The {@link InputStream} to read from
	 * @return The created {@link String}
	 */
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

	/**
	 * Creates a command from an input object.
	 * 
	 * @param request The input to parse
	 * @return A command to run
	 * @throws IOException 
	 * @throws JSONException 
	 */
	private String parseCommand(JSONObject request) throws JSONException, IOException {
		//TODO delete
		if(request.has("test")) {
			return request.getString("test");
		}
		if(request.has("pull")) {
			return "docker pull " + request.get("pull");
		} else if(request.has("run")) {
			return parseRun(request.getJSONObject("run"));
		} else if(request.has("stop")) {
			return parseStop(request.getJSONObject("stop"));
		} else if(request.has("import")) {
			return parseImport(request.getString("import"));
		}
		//TODO
		return "docker stats";
	}

	/**
	 * Parses a <pre>docker import</pre> command.
	 * Example:
	 * <pre>
	 * {
	 * 	"action": "container",
	 * 	"docker": {
	 * 		"import": [base64 container]
	 * 	}
	 * }
	 * </pre>
	 * @param string The exported container
	 * @return The parsed command
	 * @throws IOException 
	 */
	private String parseImport(String container) throws IOException {
		byte[] input = Base64.getDecoder().decode(container);
		Process process = Runtime.getRuntime().exec("docker import -");
		process.getOutputStream().write(input);
		process.getOutputStream().flush();
		new ProcessListener(process, DEFAULT_EXIT_LISTENER);
		return null;
	}

	/**
	 * Parses a <pre>docker stop</pre> command.
	 * Example:
	 * <pre>
	 * {
	 * 	"action": "container",
	 * 	"docker": {
	 * 		"stop": {
	 * 			"container": [id],
	 * 			"time": [seconds till SIGKILL]
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 * @param args The input to parse
	 * @return The parsed command
	 */
	private String parseStop(JSONObject args) {
		StringBuilder cmd = new StringBuilder("docker stop");
		if(args.has("time")) {
			cmd.append(" -t ").append(args.get("time"));
		}
		cmd.append(' ').append(args.getString("container"));
		return cmd.toString();
	}

	/**
	 * Parses a <pre>docker run</pre> command.
	 * Example:
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
	 * </pre>
	 * @param args The input to parse
	 * @return The parsed command
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

	private Process run(String cmd) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		return process;
	}

	@Override
	public JSONObject export(String container) throws Exception {
		run("docker stop " + container);
		Process process = run("docker export " + container);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(InputStream in = process.getInputStream()) {
			byte[] buffer = new byte[4096];
			int read;
			while((read = in.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
		}
		String export = Base64.getEncoder().encodeToString(out.toByteArray());
		return new JSONObject()
				.put("action", "container")
				.put("docker", new JSONObject()
						.put("import", export));
	}

	@Override
	public JSONArray getContainers() {
		JSONArray containers = new JSONArray();
		try {
			Process process = run("docker ps");
			String[] lines = copyToString(process.getInputStream()).split("\n");
			String[] headers = lines[0].split("\\s{2,}");
			for(int i = 0; i < headers.length; i++) {
				headers[i] = headers[i].trim();
			}
			for(int i = 1; i < lines.length; i++) {
				String[] values = lines[i].split("\\s{2,}");
				JSONObject container = new JSONObject();
				for(int v = 0; v < values.length; v++) {
					container.put(headers[v], values[v]);
				}
				containers.put(container);
			}
		} catch(Exception e) {
			LOG.error("Failed to get containers", e);
		}
		return containers;
	}
}
