package ru.ksu.niimm.cll.mocassin.crawl.parser.latex;

import com.google.common.base.Predicate;

public class NewtheoremCommand {
	private final String key;
	private final String title;
	private final boolean isNumbered;

	public NewtheoremCommand(String key, String title, boolean isNumbered) {
		this.key = key;
		this.title = title;
		this.isNumbered = isNumbered;
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
	}

	public boolean isNumbered() {
		return isNumbered;
	}

	public static class KeyPredicate implements Predicate<NewtheoremCommand> {
		private String key;

		public KeyPredicate(String key) {
			this.key = key;
		}

		@Override
		public boolean apply(NewtheoremCommand c) {
			if (c.getKey() == null)
				return false;
			return c.getKey().equals(this.key);
		}

	}
}
