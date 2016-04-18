package hro.inflab01.node;

/**
 * Context creator.
 * 
 * Implementations must specify a default contructor.
 */
public interface ContextProvider {
	/**
	 * Creates a new context from arguments passed to the main method.
	 * 
	 * @param args Main arguments [2, ]
	 * @return A new context
	 */
	Context createContext(String[] args);
}
