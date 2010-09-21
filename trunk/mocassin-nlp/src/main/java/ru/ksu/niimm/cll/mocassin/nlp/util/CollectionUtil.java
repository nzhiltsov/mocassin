package ru.ksu.niimm.cll.mocassin.nlp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CollectionUtil {
	private CollectionUtil() {
	}

	public static <T> List<T> asList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();
		Iterator<T> it = iterable.iterator();
		while (it.hasNext()) {
			T element = it.next();
			list.add(element);
		}
		return list;
	}
}
