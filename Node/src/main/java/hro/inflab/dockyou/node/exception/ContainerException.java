package hro.inflab.dockyou.node.exception;

public class ContainerException extends Exception {
	private static final long serialVersionUID = 4076630669693398585L;

	public ContainerException() {
		super();
	}

	public ContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ContainerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContainerException(String message) {
		super(message);
	}

	public ContainerException(Throwable cause) {
		super(cause);
	}
}
