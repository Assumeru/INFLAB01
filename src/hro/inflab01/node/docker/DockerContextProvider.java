package hro.inflab01.node.docker;

import hro.inflab01.node.Context;
import hro.inflab01.node.ContextProvider;

public class DockerContextProvider implements ContextProvider {
	@Override
	public Context createContext(String[] args) throws IllegalArgumentException {
		return new DockerContext();
	}
}
