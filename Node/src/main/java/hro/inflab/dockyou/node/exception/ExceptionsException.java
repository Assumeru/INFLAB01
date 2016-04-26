package hro.inflab.dockyou.node.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ExceptionsException extends RuntimeException implements Collection<Exception> {
	private static final long serialVersionUID = 8088593023831411631L;
	private List<Exception> exceptions = new ArrayList<>();

	@Override
	public int size() {
		return exceptions.size();
	}

	@Override
	public boolean isEmpty() {
		return exceptions.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return exceptions.contains(o);
	}

	@Override
	public Iterator<Exception> iterator() {
		return exceptions.iterator();
	}

	@Override
	public Object[] toArray() {
		return exceptions.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return exceptions.toArray(a);
	}

	@Override
	public boolean add(Exception e) {
		return exceptions.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return exceptions.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return exceptions.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Exception> c) {
		return exceptions.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return exceptions.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return exceptions.retainAll(c);
	}

	@Override
	public void clear() {
		exceptions.clear();
	}

	@Override
	public String getMessage() {
		StringWriter writer = new StringWriter();
		String message = super.getMessage();
		if(message == null) {
			message = "";
		}
		for(Exception e : exceptions) {
			writer.append("\n\t").append(e.toString());
			e.printStackTrace(new PrintWriter(writer));
		}
		return writer.toString();
	}
}
