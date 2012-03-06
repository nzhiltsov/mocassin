package ru.ksu.niimm.cll.mocassin.util;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;

public class StreamHandler<T> {
	private final Iterable<T> stream;
	private final Predicate<T> openCondition;
	private final Predicate<T> contentCondition;
	private final Predicate<T> closeCondition;

	private State state;

	enum State {
		NONE, OPEN, CONTENT;
	}

	public StreamHandler(Iterable<T> stream, Predicate<T> openCondition,
			Predicate<T> contentCondition, Predicate<T> closeCondition) {
		this.stream = stream;
		this.openCondition = openCondition;
		this.contentCondition = contentCondition;
		this.closeCondition = closeCondition;
	}

	public List<T> process() {
		List<T> contents = new LinkedList<T>();
		state = State.NONE;
		T content = null;
		for (T t : stream) {
			switch (state) {
			case NONE:
				if (openCondition.apply(t)) {
					state = State.OPEN;
				}
				break;
			case OPEN:
				if (contentCondition.apply(t)) {
					state = State.CONTENT;
					content = t;
				} else {
					state = State.NONE;
				}
				break;
			case CONTENT:
				if (closeCondition.apply(t)) {
					contents.add(content);
				}
				state = State.NONE;
				break;
			default:
				throw new UnsupportedOperationException(
						"Illegal state of the stream handler has been reached");
			}

		}
		return contents;
	}
}
