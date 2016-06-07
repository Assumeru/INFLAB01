package hro.inflab.dockyou.node.container.docker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.container.ProcessListener;
import hro.inflab.dockyou.node.container.ProcessListener.ExitListener;
import hro.inflab.dockyou.node.exception.ContainerException;
import hro.inflab.dockyou.node.exception.ProcessException;

public class DockerContext implements ContainerContext {
	private static final Logger LOG = LogManager.getLogger();
	private static final String IMPORT_COMMAND = "import";
	private static final Map<String, Function<JSONObject, String>> COMMANDS = new HashMap<>();
	private static final ExitListener DEFAULT_EXIT_LISTENER = (process, exitCode) -> {
		LOG.info("Process terminated with exit code " + exitCode);
		LOG.info(copyToString(process.getInputStream()));
		LOG.info(copyToString(process.getErrorStream()));
	};
	static {
		COMMANDS.put("pull", request -> "docker pull " + request.get("pull"));
		COMMANDS.put("run", request -> parseRun(request.getJSONObject("run")));
		COMMANDS.put("stop", request -> parseStop(request.getJSONObject("stop")));
		COMMANDS.put("restart", request -> parseRestart(request.getJSONObject("restart")));
		COMMANDS.put("start", request -> "docker start " + request.get("start"));
		COMMANDS.put("remove", request -> parseRemove(request.getJSONObject("remove")));
	}
	private List<String> startingContainers = Collections.synchronizedList(new ArrayList<>());

	@Override
	public void handle(JSONObject request) throws ContainerException {
		JSONObject args = request.getJSONObject("docker");
		try {
			runCommand(parseCommand(args));
		} catch (IOException e) {
			throw new ContainerException("Failed to run command", e);
		}
	}

	/**
	 * Runs a command using the default {@link ExitListener}.
	 * 
	 * @param command The command to run
	 * @throws IOException
	 */
	private static void runCommand(String command) throws IOException {
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
	private static void runCommand(String command, ExitListener exitListener) throws IOException {
		LOG.info(command);
		new ProcessListener(command, exitListener).listen();
	}

	/**
	 * Copies an {@link InputStream} to a {@link String}
	 * 
	 * @param in The {@link InputStream} to read from
	 * @return The created {@link String}
	 */
	private static String copyToString(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			copy(in, out, 1024);
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
	 * @throws ContainerException 
	 */
	private String parseCommand(JSONObject request) throws ContainerException {
		//TODO delete
		if(request.has("test")) {
			return request.getString("test");
		}
		for(Entry<String, Function<JSONObject, String>> entry : COMMANDS.entrySet()) {
			if(request.has(entry.getKey())) {
				return entry.getValue().apply(request);
			}
		}
		if(request.has(IMPORT_COMMAND)) {
			parseImport(request.getString(IMPORT_COMMAND));
			return null;
		}
		throw new UnsupportedOperationException("Unknown command");
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
	 * @throws ContainerException 
	 */
	private static void parseImport(String container) throws ContainerException {
		byte[] input = Base64.getDecoder().decode(container);
		try {
			Process process = Runtime.getRuntime().exec("docker import -");
			process.getOutputStream().write(input);
			process.getOutputStream().flush();
			new ProcessListener(process, DEFAULT_EXIT_LISTENER).listen();;
		} catch(IOException e) {
			throw new ContainerException("Failed to import", e);
		}
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
	private static String parseStop(JSONObject args) {
		StringBuilder cmd = new StringBuilder("docker stop");
		if(args.has("time")) {
			cmd.append(" -t ").append(args.get("time"));
		}
		cmd.append(' ').append(args.getString("container"));
		return cmd.toString();
	}

	private static String parseRestart(JSONObject args) {
		StringBuilder cmd = new StringBuilder("docker restart");
		if(args.has("time")) {
			cmd.append(" -t ").append(args.get("time"));
		}
		cmd.append(' ').append(args.getString("container"));
		return cmd.toString();
	}

	private static String parseRemove(JSONObject args) {
		StringBuilder cmd = new StringBuilder("docker rm");
		if(args.has("force") && args.getBoolean("force")) {
			cmd.append(" -f");
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
	 * 			"ports": {
	 * 				[node]: [container]
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
	private static String parseRun(JSONObject args) {
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
		if(args.has("ports")) {
			JSONObject ports = args.getJSONObject("ports");
			for(String node : ports.keySet()) {
				cmd.append(" -p ").append(node).append(':').append(ports.get(node));
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
					stopContainer(process);
				}
			});
		} catch(Exception e) {
			LOG.error("Failed to stop all containers", e);
		}
	}

	private static void stopContainer(Process process) {
		try {
			runCommand("docker stop " + String.join(" ", copyToString(process.getInputStream()).split("\n")));
		} catch (Exception e) {
			throw new ProcessException("Failed to run command", e);
		}
	}

	private Process run(String cmd) throws IOException, InterruptedException {
		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();
		return process;
	}

	@Override
	public JSONObject export(String container) throws ContainerException {
		ByteArrayOutputStream out;
		try {
			out = exportInternal(container);
		} catch (IOException | InterruptedException e) {
			throw new ContainerException("Failed to export", e);
		}
		String export = Base64.getEncoder().encodeToString(out.toByteArray());
		return new JSONObject()
				.put("action", "container")
				.put("docker", new JSONObject()
						.put(IMPORT_COMMAND, export));
	}

	private ByteArrayOutputStream exportInternal(String container) throws IOException, InterruptedException {
		LOG.info("Stopping container " + container);
		run("docker stop " + container);
		LOG.info("Exporting container " + container);
		Process process = run("docker export " + container);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(InputStream in = process.getInputStream()) {
			copy(in, out, 4096);
		}
		LOG.info("Exported image size: " + out.size());
		return out;
	}

	private static void copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
		byte[] buffer = new byte[bufferSize];
		int read;
		while((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
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

	@Override
	public JSONArray getStartingContainers() {
		synchronized(startingContainers) {
			return new JSONArray(startingContainers);
		}
	}
}
