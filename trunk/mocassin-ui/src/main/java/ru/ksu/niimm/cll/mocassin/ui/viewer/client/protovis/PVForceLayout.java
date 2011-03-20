package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;


import org.thechiselgroup.choosel.protovis.client.PVAbstractBar;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link;

import com.google.gwt.core.client.JavaScriptObject;

public class PVForceLayout extends PVAbstractBar<PVForceLayout> {
	protected PVForceLayout() {
	}

	private final native PVForceLayout nodes(JavaScriptObject nodes) /*-{
		return this.nodes(nodes);
	}-*/;

	public final <S> PVForceLayout nodes(PVNodeAdapter<S> adapter, S... nodes) {
		assert adapter != null;
		assert nodes != null;

		JsArrayGeneric<PVNode> jsNodes = JsUtils.createJsArrayGeneric();
		for (S node : nodes) {
			PVNode pvNode = PVNode.create(node);
			String nodeName = adapter.getNodeName(node);
			if (nodeName != null) {
				pvNode.nodeName(nodeName);
			}
			Object nodeValue = adapter.getNodeValue(node);
			if (nodeValue != null) {
				pvNode.nodeValue(nodeValue);
			}
			jsNodes.push(pvNode);
		}

		return this.nodes(jsNodes);
	}

	/**
	 * @param links
	 *            JavaScript array of java objects
	 */
	private final native PVForceLayout links(JavaScriptObject links) /*-{
		return this.links(links);
	}-*/;

	public final PVForceLayout links(Link... links) {
		JsArrayGeneric<PVLink> jsLinks = JsUtils.createJsArrayGeneric();
		for (Link link : links) {
			jsLinks.push(PVLink.create(link));
		}
		return this.links(jsLinks);
	}

	public final native PVMark label() /*-{
		return this.label;
	}-*/;

	public final native PVMark link() /*-{
		return this.link;
	}-*/;

	public final native PVMark node() /*-{
		return this.node;
	}-*/;
}
