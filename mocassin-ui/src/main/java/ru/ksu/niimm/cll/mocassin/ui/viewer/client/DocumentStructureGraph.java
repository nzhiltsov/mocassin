package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import org.thechiselgroup.choosel.protovis.client.Link;
import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVColor;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVForceLayout;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVNode;
import org.thechiselgroup.choosel.protovis.client.PVOrdinalScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.ProtovisWidget;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node.NovelCharacterNodeAdapter;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.LinkAdapter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class DocumentStructureGraph extends ProtovisWidget {

	private final ViewerServiceAsync viewerService = GWT
			.create(ViewerService.class);

	private Frame frame;

	private String resourceUri;

	private String pdfUri;
	private NavigationPopup navigationPopup;

	/*
	 * private static final PVColor[] code2color = { PV.color("red"),
	 * PV.color("orange"), PV.color("fuchsia"), PV.color("teal"),
	 * PV.color("navy"), PV.color("gray"), PV.color("purple"),
	 * PV.color("maroon"), PV.color("lime"), PV.color("yellow"),
	 * PV.color("violet"), PV.color("aqua"), PV.color("blue"),
	 * PV.color("green"), PV.color("black") };
	 */

	public DocumentStructureGraph(Frame frame, String resourceUri, String pdfUri) {
		super();
		this.frame = frame;
		this.resourceUri = resourceUri;
		this.pdfUri = pdfUri;
		this.navigationPopup = new NavigationPopup(frame, pdfUri);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	private void createVisualization(Node[] nodes, Link[] links) {
		PVPanel vis = getPVPanel().width(450).height(450).fillStyle("white")
				.event(PV.Event.MOUSEDOWN, PV.Behavior.pan())
				.event(PV.Event.MOUSEWHEEL, PV.Behavior.zoom());

		PVForceLayout force = vis.add(PV.Layout.Force())
				.nodes(new NovelCharacterNodeAdapter(), nodes).links(links);

		force.link().add(PV.Line);

		force.node()
				.add(PV.Dot)
				.size(new JsDoubleFunction() {
					public double f(JsArgs args) {
						PVNode d = args.getObject();
						return (20 * d.linkDegree() + 4)
								* (Math.pow(args.<PVMark> getThis().scale(),
										-1.5));
					}
				})
				.fillStyle(new JsFunction<PVColor>() {
					private PVOrdinalScale colors = PV.Colors.category19();

					public PVColor f(JsArgs args) {
						PVNode d = args.getObject();
						if (d.fix()) {
							return PV.color("brown");
						}
						PVColor color = colors.fcolor(d.<Node> object()
								.getNodeType());

						return color;
					}
				}).strokeStyle(new JsFunction<PVColor>() {
					public PVColor f(JsArgs args) {
						PVDot _this = args.getThis();
						return _this.fillStyle().darker();
					}
				}).lineWidth(1).title(new JsStringFunction() {
					public String f(JsArgs args) {
						PVNode d = args.getObject();
						return d.nodeName();
					}
				}).event(PV.Event.MOUSEDOWN, PV.Behavior.drag())
				.event(PV.Event.DRAG, force)
				.event(PV.Event.MOUSEDOWN, new PVEventHandler() {

					@Override
					public void onEvent(Event e, String pvEventType, JsArgs args) {
						PVNode d = args.getObject();
						String numPage = Integer.toString(d.<Node> object()
								.getNumPage() - 1);
						navigationPopup.setNumPage(numPage);
						Element node = e.getCurrentTarget();
						navigationPopup.setPopupPosition(
								node.getAbsoluteLeft(), node.getAbsoluteTop());
						navigationPopup.show();
					}
				});
		/*
		 * new PVEventHandler() { public void onEvent(Event e, String
		 * pvEventType, JsArgs args) { PVNode d = args.getObject();
		 * 
		 * String numPage = Integer.toString(d.<Node> object() .getNumPage() -
		 * 1); getFrame().setUrl( "http://docs.google.com/viewer?url=" + pdfUri
		 * + "&embedded=true#:0.page." + numPage); }
		 */

	}

	protected void onAttach() {
		super.onAttach();
		initPVPanel();
		AsyncCallback<Graph> callback = new AsyncCallback<Graph>() {

			@Override
			public void onSuccess(Graph result) {
				LinkAdapter[] l = result.getLinks();
				Link[] links = new Link[l.length];
				for (int i = 0; i < l.length; i++) {
					links[i] = new Link(l[i].getSource(), l[i].getTarget(),
							l[i].getValue());
				}
				createVisualization(result.getNodes(), links);
				getPVPanel().render();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("couldn't load the structure graph: "
						+ caught.getMessage());

			}
		};
		viewerService.retrieveGraph(this.resourceUri, callback);

	}
}
