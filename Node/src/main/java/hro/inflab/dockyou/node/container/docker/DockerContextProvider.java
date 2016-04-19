package hro.inflab.dockyou.node.container.docker;

import hro.inflab.dockyou.node.container.ContainerContext;
import hro.inflab.dockyou.node.container.ContainerContextProvider;

public class DockerContextProvider implements ContainerContextProvider {
	@Override
	public ContainerContext createContext(String[] args) throws IllegalArgumentException {
		return new DockerContext();
	}
}
