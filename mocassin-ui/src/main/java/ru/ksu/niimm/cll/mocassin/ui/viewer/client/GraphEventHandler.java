package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.event.shared.EventHandler;

public interface GraphEventHandler extends EventHandler {
	void onNodeChange(GraphEvent event);
}
