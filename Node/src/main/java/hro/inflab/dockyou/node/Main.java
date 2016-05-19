package hro.inflab.dockyou.node;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.container.docker.DockerContext;

public class Main {
	private static final Logger LOG = LogManager.getLogger();
	private static final Class<? extends ContainerContext> DEFAULT_CONTAINER_CONTEXT = DockerContext.class;

	/**
	 * Program entry point.
	 * Creates and runs a {@link Node}.
	 * 
	 * @param args First argument must be the manager's URL
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			exit("usage: <manager url>");
		}
		try {
			URL managerUrl = createUrlOrDie(args[0]);
			ContainerContext context = createContextOrDie();
			if(context == null) {
				exit("Failed to start.");
			}
			new Node(managerUrl, context).run();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			System.exit(2);
		}
	}

	/**
	 * Logs a message and calls System.exit.
	 * 
	 * @param message The message to log
	 */
	private static void exit(String message) {
		LOG.error(message);
		System.exit(1);
	}

	/**
	 * Creates a URL from the given string.
	 * Calls System.exit if an exception occurs.
	 * 
	 * @param url The string to create a URL from
	 * @return The created URL
	 */
	private static URL createUrlOrDie(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			exit("Invalid manager url: " + url);
		}
		return null;
	}

	/**
	 * Creates a {@link ContainerContext}.
	 * Calls System.exit if an exception occurs.
	 * 
	 * @return The created context
	 */
	private static ContainerContext createContextOrDie() {
		try {
			return createContext();
		} catch (ClassNotFoundException e) {
			exit("Failed to load the provided class.");
		} catch (InstantiationException | IllegalAccessException | ClassCastException e) {
			exit("Failed to instantiate the provided class.");
		} catch (IllegalArgumentException e) {
			exit(e.getMessage());
		}
		return null;
	}

	/**
	 * Creates a context using a JVM argument or using the default.
	 * 
	 * @return The created context
	 * @throws ClassNotFoundException If the specified class cannot be found
	 * @throws InstantiationException If the class cannot be instantiated
	 * @throws IllegalAccessException If the constructor is inaccessible
	 */
	private static ContainerContext createContext() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String arg = ContainerContext.class.getName();
		String className = System.getProperty(arg);
		Class<?> type;
		if(className == null || className.isEmpty()) {
			LOG.warn("Using default ContainerContext, use jvm argument -D" + arg + "=<class> to defined another implementation.");
			type = DEFAULT_CONTAINER_CONTEXT;
		} else {
			type = Class.forName(className);
			if(!ContainerContext.class.isAssignableFrom(type)) {
				throw new IllegalArgumentException(type + " does not implement " + ContainerContext.class);
			}
		}
		return (ContainerContext) type.newInstance();
	}
}
