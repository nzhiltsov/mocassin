package ru.ksu.niimm.cll.mocassin.parser.latex;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import net.sourceforge.texlipse.model.TexCommandEntry;
import ru.ksu.niimm.cll.mocassin.util.StringUtil;

public class TexCommandEntryAdapter {
	private TexCommandEntry entry;
	private String title;

	public TexCommandEntryAdapter(TexCommandEntry entry) {
		this.entry = entry;
		this.title = takeoutMarkup(entry.info);
	}

	private String takeoutMarkup(String str) {
		return StringUtil.takeoutMarkup(str).toLowerCase();
	}

	public TexCommandEntry getEntry() {
		return entry;
	}

	public String getTitle() {
		return title;
	}

	public static class TransformFunction implements
			Function<TexCommandEntry, TexCommandEntryAdapter> {

		@Override
		public TexCommandEntryAdapter apply(TexCommandEntry entry) {
			return new TexCommandEntryAdapter(entry);
		}

	}

	public static class KeyPredicate implements
			Predicate<TexCommandEntryAdapter> {
		private String key;

		public KeyPredicate(String key) {
			this.key = key;
		}

		@Override
		public boolean apply(TexCommandEntryAdapter c) {
			if (c.getEntry().key == null)
				return false;
			return c.getEntry().key.equals(this.key);
		}

	}
}
