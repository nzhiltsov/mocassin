package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

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

import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.Node.NovelCharacterNodeAdapter;
import ru.ksu.niimm.cll.mocassin.frontend.viewer.client.protovis.LinkAdapter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DocumentStructureGraph extends Composite implements
		GraphEventHandler {
	@UiTemplate("DocumentStructureGraph.ui.xml")
	interface Binder extends UiBinder<VerticalPanel, DocumentStructureGraph> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private final ViewerServiceAsync viewerService = GWT
			.create(ViewerService.class);

	NodeExplorer nodeExplorer = new NodeExplorer();
	@UiField
	ProtovisWidget protovisWidget;

	private String resourceUri;

	private String currentSegmentUri;

	private Graph graph;

	public DocumentStructureGraph() {
		initWidget(uiBinder.createAndBindUi(this));
		App.eventBus.addHandler(GraphEvent.TYPE, this);
	}

	public void refresh(ArticleInfo info, EnumMap<Relations, Boolean> filters) {
		this.resourceUri = info.getUri();
		this.currentSegmentUri = info.getCurrentSegmentUri();
		protovisWidget.initPVPanel();
		loadData(filters);
	}

	public void refresh(EnumMap<Relations, Boolean> filters) {
		protovisWidget.initPVPanel();
		loadData(filters);
	}

	private String getCurrentSegmentUri() {
		return currentSegmentUri;
	}

	private void setCurrentSegmentUri(String currentSegmentUri) {
		this.currentSegmentUri = currentSegmentUri;
	}

	private void createVisualization(Node[] nodes, Link[] links) {
		PVPanel vis = protovisWidget.getPVPanel().width(480).height(350)
				.fillStyle("white")
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

					@Override
					public void onEvent(Event e, String pvEventType, JsArgs args) {
						PVNode d = args.getObject();
						Node currentNode = d.<Node> object();

						showNodeExplorer(currentNode);
					}

				});

	}

	private void loadData(final EnumMap<Relations, Boolean> filters) {
		AsyncCallback<Graph> callback = new AsyncCallback<Graph>() {

			@Override
			public void onSuccess(Graph result) {
				graph = result;
				LinkAdapter[] linkData = result.getLinks();
				List<Link> linkList = new LinkedList<Link>();

				for (int i = 0; i < linkData.length; i++) {
					Relations relType = Relations.fromCode(linkData[i]
							.getType());
					if (relType == null) continue;
					if (relType != null && !filters.get(relType)) {
						continue;
					}

					linkList.add(new Link(linkData[i].getSource(), linkData[i]
							.getTarget(), linkData[i].getValue()));

				}
				Link[] links = linkList.toArray(new Link[linkList.size()]);
				createVisualization(result.getNodes(), links);
				protovisWidget.getPVPanel().render();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("couldn't load the structure graph: "
						+ caught.getMessage());

			}
		};
		viewerService.retrieveGraph(this.resourceUri, callback);
	}

	private void showNodeExplorer(Node currentNode) {
		setCurrentSegmentUri(currentNode.getUri());
		nodeExplorer.setCurrentNode(currentNode);
		List<RelationMetadata> relations = new ArrayList<RelationMetadata>();
		LinkAdapter[] linkData = graph.getLinks();
		Node[] nodes = graph.getNodes();
		for (LinkAdapter link : linkData) {
			RelationMetadata relation = null;
			if (nodes[link.getSource()] == currentNode) {
				relation = new RelationMetadata(currentNode,
						nodes[link.getTarget()], Relations.fromCode(link
								.getType()));
			} else if (nodes[link.getTarget()] == currentNode) {
				relation = new RelationMetadata(nodes[link.getSource()],
						currentNode, Relations.fromCode(link.getType()));
			}
			if (relation != null) {
				relations.add(relation);
			}
		}
		nodeExplorer.setRelations(relations);
		nodeExplorer.show();
	}

	@Override
	public void onNodeChange(GraphEvent event) {
		showNodeExplorer(event.getCurrentNode());
	}

}
