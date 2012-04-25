package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class App {
	public static EventBus eventBus = GWT.create(SimpleEventBus.class);
}
