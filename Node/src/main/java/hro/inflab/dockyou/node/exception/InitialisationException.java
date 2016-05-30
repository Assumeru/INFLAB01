package hro.inflab.dockyou.node.exception;

public class InitialisationException extends RuntimeException {
	private static final long serialVersionUID = 2142875724302620808L;

	public InitialisationException() {
		super();
	}

	public InitialisationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InitialisationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitialisationException(String message) {
		super(message);
	}

	public InitialisationException(Throwable cause) {
		super(cause);
	}
}
