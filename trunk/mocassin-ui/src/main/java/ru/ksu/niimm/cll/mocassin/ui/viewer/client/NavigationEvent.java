package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationEvent extends GwtEvent<NavigationEventHandler> {
	private final int numPage;

	public static final Type<NavigationEventHandler> TYPE = new Type<NavigationEventHandler>();

	NavigationEvent(int numPage) {
		super();
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

	public int getNumPage() {
		return numPage;
	}

}
