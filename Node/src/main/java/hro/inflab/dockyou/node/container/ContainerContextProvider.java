package hro.inflab.dockyou.node.container;

/**
 * Context creator.
 * 
 * Implementations must specify a default contructor.
 */
public interface ContainerContextProvider {
	/**
	 * Creates a new context from arguments passed to the main method.
	 * 
	 * @param args Main arguments [2, ]
	 * @return A new context
	 */
	ContainerContext createContext(String[] args);
}
