package ru.ksu.niimm.cll.mocassin.util;

import java.util.List;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

public class StreamHandlerTest {
	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]+");

	@Test
	public void testProcess() {
		List<String> values = ImmutableList.of("[", "14", "]", "и", "[", "15",
				"]", "[", "0", ",", "1", "]");
		Predicate<String> openCondition = new Predicate<String>() {

			@Override
			public boolean apply(String input) {
				return input.equals("[");
			}
		};
		Predicate<String> contentCondition = new Predicate<String>() {

			@Override
			public boolean apply(String input) {
				return NUMBER_PATTERN.matcher(input).matches();
			}
		};
		Predicate<String> closeCondition = new Predicate<String>() {

			@Override
			public boolean apply(String input) {
				return input.equals("]");
			}
		};

		StreamHandler<String> streamHandler = new StreamHandler<String>(values,
				openCondition, contentCondition, closeCondition);
		List<String> contents = streamHandler.process();
		Assert.assertEquals(2, contents.size());
		Assert.assertTrue(contents.contains("14"));
		Assert.assertTrue(contents.contains("15"));
	}
}
