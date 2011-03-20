package ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis;

import org.thechiselgroup.choosel.protovis.client.PVBulletLayout;
import org.thechiselgroup.choosel.protovis.client.PVClusterLayout;
import org.thechiselgroup.choosel.protovis.client.PVFillPartitionLayout;
import org.thechiselgroup.choosel.protovis.client.PVPackLayout;
import org.thechiselgroup.choosel.protovis.client.PVStackLayout;
import org.thechiselgroup.choosel.protovis.client.PVTreeLayout;
import org.thechiselgroup.choosel.protovis.client.PVTreemapLayout;


public final class PVLayout {

	public static native PVBulletLayout Bullet() /*-{
		return $wnd.pv.Layout.Bullet;
	}-*/;

	public static native PVClusterLayout Cluster() /*-{
		return $wnd.pv.Layout.Cluster;
	}-*/;

	public static native PVPackLayout Pack() /*-{
		return $wnd.pv.Layout.Pack;
	}-*/;

	public static native PVFillPartitionLayout PartitionFill() /*-{
		return $wnd.pv.Layout.Partition.Fill;
	}-*/;

	public static native PVStackLayout Stack() /*-{
		return $wnd.pv.Layout.Stack;
	}-*/;

	public static native PVTreeLayout Tree() /*-{
		return $wnd.pv.Layout.Tree;
	}-*/;

	public static native PVTreemapLayout Treemap() /*-{
		return $wnd.pv.Layout.Treemap;
	}-*/;

	public static native PVArcLayout Arc() /*-{
		return $wnd.pv.Layout.Arc;
	}-*/;

	public static native PVForceLayout Force() /*-{
		return $wnd.pv.Layout.Force;
	}-*/;

	private PVLayout() {
	}

}
