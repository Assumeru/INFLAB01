package hro.inflab.dockyou.node.exception;

public class ActionException extends Exception {
	private static final long serialVersionUID = -5967713576086664268L;

	public ActionException() {
		super();
	}

	public ActionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionException(String message) {
		super(message);
	}

	public ActionException(Throwable cause) {
		super(cause);
	}
}
