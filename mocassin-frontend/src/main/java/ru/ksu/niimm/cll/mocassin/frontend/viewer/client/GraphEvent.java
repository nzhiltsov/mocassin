package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import com.google.gwt.event.shared.GwtEvent;

public class GraphEvent extends GwtEvent<GraphEventHandler> {
	public static final Type<GraphEventHandler> TYPE = new Type<GraphEventHandler>();

	private final Node currentNode;

	public GraphEvent(Node currentNode) {
		super();
		this.currentNode = currentNode;
	}

	public Node getCurrentNode() {
		return currentNode;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GraphEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GraphEventHandler handler) {
		handler.onNodeChange(this);
	}

}
