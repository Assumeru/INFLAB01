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

	public static void main(String[] args) {
		if(args.length == 0) {
			exit("usage: <manager url>");
			return;
		}
		try {
			URL managerUrl = createUrlOrDie(args[0]);
			ContainerContext context = createContextOrDie();
			if(context == null) {
				exit("Failed to start.");
				return;
			}
			new Node(managerUrl, context).run();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			System.exit(2);
		}
	}

	private static void exit(String message) {
		LOG.error(message);
		System.exit(1);
	}

	private static URL createUrlOrDie(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			exit("Invalid manager url: " + url);
		}
		return null;
	}

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
