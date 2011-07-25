package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationEvent extends GwtEvent<NavigationEventHandler> {
	private final int numPage;
	private final String currentUri;

	public static final Type<NavigationEventHandler> TYPE = new Type<NavigationEventHandler>();

	NavigationEvent(String currentUri, int numPage) {
		super();
		this.currentUri = currentUri;
		this.numPage = numPage;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<NavigationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(NavigationEventHandler handler) {
		handler.onChange(this);

	}

	public String getCurrentUri() {
		return currentUri;
	}

	public int getNumPage() {
		return numPage;
	}

}
