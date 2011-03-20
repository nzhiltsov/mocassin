package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import java.util.Comparator;

import org.thechiselgroup.choosel.protovis.client.PVAbstractBar;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArrayGeneric;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsUtils;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Link;

import com.google.gwt.core.client.JavaScriptObject;

public final class PVArcLayout extends PVAbstractBar<PVArcLayout> {

	protected PVArcLayout() {
	}

	public final native PVMark label() /*-{
		return this.label;
	}-*/;

	public final native PVMark link() /*-{
		return this.link;
	}-*/;

	/**
	 * @param links
	 *            JavaScript array of java objects
	 */
	private final native PVArcLayout links(JavaScriptObject links) /*-{
		return this.links(links);
	}-*/;

	public final PVArcLayout links(Link... links) {
		JsArrayGeneric<PVLink> jsLinks = JsUtils.createJsArrayGeneric();
		for (Link link : links) {
			jsLinks.push(PVLink.create(link));
		}
		return this.links(jsLinks);
	}

	public final native PVMark node() /*-{
		return this.node;
	}-*/;

	private final native PVArcLayout nodes(JavaScriptObject nodes) /*-{
		return this.nodes(nodes);
	}-*/;

	public final <S> PVArcLayout nodes(PVNodeAdapter<S> adapter, S... nodes) {
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

	// TODO String[] as nodes
	// TODO double[] / int[] as nodes

	public final native String orient() /*-{
		return this.orient;
	}-*/;

	public final native PVArcLayout orient(String orient) /*-{
		return this.orient(orient);
	}-*/;

	public PVArcLayout sort(Comparator<PVNode> comparator) {
		return this.sort(JsUtils.toJsComparator(comparator));
	}

	private final native PVArcLayout sort(JavaScriptObject comparator) /*-{
		return this.sort(comparator);
	}-*/;

}