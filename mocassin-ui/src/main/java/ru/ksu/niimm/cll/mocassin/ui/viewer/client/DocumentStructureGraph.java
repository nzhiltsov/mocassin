package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

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

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.DocumentStructureGraphPanel.Binder;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.DocumentStructureGraphPanel.Relations;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.Node.NovelCharacterNodeAdapter;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.LinkAdapter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class DocumentStructureGraph extends ProtovisWidget {
	@UiTemplate("DocumentStructureGraph.ui.xml")
	interface Binder extends
			UiBinder<DocumentStructureGraph, DocumentStructureGraph> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private final ViewerServiceAsync viewerService = GWT
			.create(ViewerService.class);
	private String resourceUri;

	private String currentSegmentUri;

	public DocumentStructureGraph() {
		super();
		uiBinder.createAndBindUi(this);
	}

	public void refresh(ArticleInfo info, EnumMap<Relations, Boolean> filters) {
		this.resourceUri = info.getUri();
		this.currentSegmentUri = info.getCurrentSegmentUri();
		initPVPanel();
		loadData(filters);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	private String getCurrentSegmentUri() {
		return currentSegmentUri;
	}

	private void setCurrentSegmentUri(String currentSegmentUri) {
		this.currentSegmentUri = currentSegmentUri;
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
						if (d.<Node> object().getNodeType() == 14) {// the node
																	// is not
																	// recognized
							return 24 * (Math.pow(args.<PVMark> getThis()
									.scale(), -1.5));
						}
						return (20 * d.linkDegree() + 4)
								* (Math.pow(args.<PVMark> getThis().scale(),
										-1.5));
					}
				})
				.fillStyle(new JsFunction<PVColor>() {
					private PVOrdinalScale colors = PV.Colors.category19();

					public PVColor f(JsArgs args) {
						PVNode d = args.getObject();
						String nodeUri = d.<Node> object().getUri();
						if (d.fix() || nodeUri.equals(getCurrentSegmentUri())) {
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

					@SuppressWarnings("deprecation")
					@Override
					public void onEvent(Event e, String pvEventType, JsArgs args) {
						PVNode d = args.getObject();

						Node currentNode = d.<Node> object();
						setCurrentSegmentUri(currentNode.getUri());
						int numPage = currentNode.getNumPage();
						
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

	private void loadData(final EnumMap<Relations, Boolean> filters) {
		AsyncCallback<Graph> callback = new AsyncCallback<Graph>() {

			@Override
			public void onSuccess(Graph result) {
				LinkAdapter[] l = result.getLinks();
				List<Link> linkList = new LinkedList<Link>();

				for (int i = 0; i < l.length; i++) {
					// see MocassinOntologyRelations for predicate codes
					if (l[i].getType() == 0 && !filters.get(Relations.hasPart)) {
						continue;
					} else if (l[i].getType() == 8
							&& !filters.get(Relations.dependsOn)) {
						continue;
					} else if (l[i].getType() == 1
							&& !filters.get(Relations.refersTo)) {
						continue;
					} else if (l[i].getType() == 9
							&& !filters.get(Relations.proves)) {
						continue;
					} else if (l[i].getType() == 2
							&& !filters.get(Relations.hasConsequence)) {
						continue;
					} else if (l[i].getType() == 3
							&& !filters.get(Relations.exemplifies)) {
						continue;
					}
					linkList.add(new Link(l[i].getSource(), l[i].getTarget(),
							l[i].getValue()));

				}
				Link[] links = linkList.toArray(new Link[linkList.size()]);
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
