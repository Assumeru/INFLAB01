package hro.inflab.dockyou.node.docker;

import hro.inflab.dockyou.node.Context;
import hro.inflab.dockyou.node.ContextProvider;

public class DockerContextProvider implements ContextProvider {
	@Override
	public Context createContext(String[] args) throws IllegalArgumentException {
		return new DockerContext();
	}
}
