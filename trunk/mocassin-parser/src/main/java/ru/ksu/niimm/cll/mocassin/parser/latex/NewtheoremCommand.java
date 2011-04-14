package ru.ksu.niimm.cll.mocassin.parser.latex;

import com.google.common.base.Predicate;

public class NewtheoremCommand {
	private String key;
	private String title;

	public NewtheoremCommand(String key, String title) {
		this.key = key;
		this.title = title.toLowerCase();
	}

	public String getKey() {
		return key;
	}

	public String getTitle() {
		return title;
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
