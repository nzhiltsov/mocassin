package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import org.thechiselgroup.choosel.protovis.client.PV;
import org.thechiselgroup.choosel.protovis.client.PVColor;
import org.thechiselgroup.choosel.protovis.client.PVColors;
import org.thechiselgroup.choosel.protovis.client.PVDot;
import org.thechiselgroup.choosel.protovis.client.PVEventHandler;
import org.thechiselgroup.choosel.protovis.client.PVEventType;
import org.thechiselgroup.choosel.protovis.client.PVMark;
import org.thechiselgroup.choosel.protovis.client.PVOrdinalScale;
import org.thechiselgroup.choosel.protovis.client.PVPanel;
import org.thechiselgroup.choosel.protovis.client.ProtovisWidget;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsArgs;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsDoubleFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsFunction;
import org.thechiselgroup.choosel.protovis.client.jsutil.JsStringFunction;

import ru.ksu.niimm.cll.mocassin.ui.viewer.client.GraphStub.DocumentSegment;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVBehavior2;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVForceLayout;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVLayout;
import ru.ksu.niimm.cll.mocassin.ui.viewer.client.protovis.PVNode;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

public class DocumentStructureGraph extends ProtovisWidget {
	private Frame frame;

	public Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	private void createVisualization(DocumentSegment[] nodes, Link[] links) {
		final PVOrdinalScale colors = PVColors.category19();
		PVPanel vis = getPVPanel().width(280).height(350).fillStyle("white")
				.event(PVEventType.MOUSEDOWN, PVBehavior2.pan()).event(
						"mousewheel", PVBehavior2.zoom());

		PVForceLayout force = vis.add(PVLayout.Force()).nodes(
				new GraphStub.NovelCharacterNodeAdapter(), nodes).links(links);

		force.link().add(PV.Line);

		force.node().add(PV.Dot).size(new JsDoubleFunction() {
			public double f(JsArgs args) {
				PVNode d = args.getObject();
				return (20*d.linkDegree() + 4)
						* (Math.pow(args.<PVMark> getThis().scale(), -1.5));
			}
		}).fillStyle(new JsFunction<PVColor>() {
			public PVColor f(JsArgs args) {
				PVNode d = args.getObject();
				PVColor color = colors.fcolor(d.<DocumentSegment> object()
						.getType().getCode());

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
		}).event("mousedown", new PVEventHandler() {
			public void onEvent(Event e, String pvEventType, JsArgs args) {
				PVNode d = args.getObject();

				String numPage = Integer.toString(d.<DocumentSegment> object()
						.getNumPage() - 1);
				getFrame()
						.setUrl(
								"http://docs.google.com/viewer?url=http://arxiv.org/pdf/math/0205003v1&embedded=true#:0.page."
										+ numPage);
			}
		});

	}

	protected void onAttach() {
		super.onAttach();
		initPVPanel();
		createVisualization(GraphStub.CHARACTERS, GraphStub.LINKS);
		getPVPanel().render();
	}

	public String toString() {
		return "Arc Diagram";
	}
}
