package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import com.google.gwt.core.client.JavaScriptObject;

public class PVNode extends JavaScriptObject {

	public static native PVNode create(Object original) /*-{
		return {
			'object' : original
		};
	}-*/;

	protected PVNode() {
	}

	public final native int linkDegree() /*-{
		return this.linkDegree;
	}-*/;

	

	public final native String nodeName() /*-{
		return this.nodeName;
	}-*/;

	public final native PVNode nodeName(String nodeName) /*-{
		this.nodeName = nodeName;
		return this;
	}-*/;

	public final native <T> PVNode nodeValue(T nodeValue) /*-{
		this.nodeValue = nodeValue;
		return this;
	}-*/;

	public final native <T> T object() /*-{
		return this.object;
	}-*/;

	/**
	 * X coordinate of node.
	 */
	public final native double x() /*-{
		return this.x;
	}-*/;

	/**
	 * Y coordinate of node.
	 */
	public final native double y() /*-{
		return this.y;
	}-*/;

}
