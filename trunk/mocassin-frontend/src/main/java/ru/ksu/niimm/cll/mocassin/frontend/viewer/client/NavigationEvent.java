/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

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
