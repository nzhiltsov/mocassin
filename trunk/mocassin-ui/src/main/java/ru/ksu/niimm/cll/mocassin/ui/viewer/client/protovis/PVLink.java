package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link;

import com.google.gwt.core.client.JavaScriptObject;

public class PVLink extends JavaScriptObject {

	public static native PVLink create(int source, int target, double value) /*-{
		return {
			'source' : source,
			'target' : target,
			'value' : value
		};
	}-*/;

	public static PVLink create(Link link) {
		return create(link.getSource(), link.getTarget(), link.getValue());
	}

	protected PVLink() {
	}

	public final native int source() /*-{
		return this.source;
	}-*/;

	public final native PVNode sourceNode() /*-{
		return this.sourceNode;
	}-*/;

	public final native int target() /*-{
		return this.target;
	}-*/;

	public final native PVNode targetNode() /*-{
		return this.targetNode;
	}-*/;

}