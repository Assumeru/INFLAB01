package hro.inflab.dockyou.node;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.container.ContainerContextProvider;

public class Main {
	public static void main(String[] args) {
		if(args.length < 2) {
			exit("usage: <manager url> <container context provider class>");
			return;
		}
		try {
			URL managerUrl = createUrlOrDie(args[0]);
			ContainerContext context = createContextOrDie(args);
			if(context == null) {
				exit("Failed to start.");
				return;
			}
			new Node(managerUrl, context).run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

	private static void exit(String message) {
		System.err.println(message);
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

	private static ContainerContext createContextOrDie(String[] args) {
		try {
			return createContext(args);
		} catch (ClassNotFoundException e) {
			exit("Failed to load the provided class.");
		} catch (InstantiationException | IllegalAccessException | ClassCastException e) {
			exit("Failed to instantiate the provided class.");
		} catch (IllegalArgumentException e) {
			exit(e.getMessage());
		}
		return null;
	}

	private static ContainerContext createContext(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> type = Class.forName(args[1]);
		if(!ContainerContextProvider.class.isAssignableFrom(type)) {
			throw new IllegalArgumentException(type + " does not implement " + ContainerContextProvider.class);
		}
		ContainerContextProvider provider = (ContainerContextProvider) type.newInstance();
		return provider.createContext(Arrays.copyOfRange(args, 2, args.length));
	}
}
