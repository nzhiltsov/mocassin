package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import org.thechiselgroup.choosel.protovis.client.PVBehavior;

public class PVBehavior2 extends
		org.thechiselgroup.choosel.protovis.client.PVBehavior {
	protected PVBehavior2() {
	}

	public final static native PVBehavior zoom() /*-{
		return $wnd.pv.Behavior.zoom();
	}-*/;

	public final static native PVBehavior pan() /*-{
		return $wnd.pv.Behavior.pan();
	}-*/;
	
	public final static native PVBehavior drag() /*-{
	return $wnd.pv.Behavior.drag();
}-*/;
}
