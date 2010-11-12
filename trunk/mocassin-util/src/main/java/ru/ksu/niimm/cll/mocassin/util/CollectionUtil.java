package ru.ksu.niimm.cll.mocassin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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

	public static <T> List<T> sampleRandomSublist(List<T> list, int sampleSize) {
		int size = list.size();
		if (sampleSize >= size) {
			List<T> sublist = new ArrayList<T>(list);
			Collections.shuffle(sublist);
			return sublist;
		}
		List<T> sublist = new ArrayList<T>();
		Random random = new Random();
		int s = 0;
		while (s < sampleSize) {
			int index = random.nextInt(size);
			T element = list.get(index);
			if (!sublist.contains(element)) {
				sublist.add(element);
				s++;
			}
		}
		return sublist;
	}

}
