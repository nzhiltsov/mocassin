package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class NodeExplorer {
	@UiTemplate("NodeExplorer.ui.xml")
	interface Binder extends UiBinder<DialogBox, NodeExplorer> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private ViewerConstants constants = GWT.create(ViewerConstants.class);

	@UiField
	Button closeButton;
	@UiField
	Label titleLabel;
	@UiField
	Label uriLabel;
	@UiField
	Label typeLabel;
	@UiField
	Anchor goLink;
	@UiField
	DialogBox dialog;
	@UiField
	FlexTable relationTable;

	private Node currentNode;

	private List<RelationMetadata> relations;

	public NodeExplorer() {
		uiBinder.createAndBindUi(this);
	}

	public void show() {
		titleLabel.setText(currentNode.getName());
		uriLabel.setText(currentNode.getUri());
		typeLabel.setText(SegmentTypes.getLabel(currentNode.getNodeType()));
		if (currentNode.getNumPage() == 0) {
			goLink.setVisible(false);
		} else {
			goLink.setVisible(true);
			goLink.setText(constants.goLink() + " " + currentNode.getNumPage());
		}
		relationTable.clear();
		relationTable.setWidget(0, 0, new HTML("<b>Domain</b>"));
		relationTable.setWidget(0, 1, new HTML("<b>Relation</b>"));
		relationTable.setWidget(0, 2, new HTML("<b>Range</b>"));
		int i = 1;
		for (final RelationMetadata rel : relations) {
			boolean isCurrentFirst = rel.getFrom() == currentNode;
			Label from = null;
			Label to = null;
			if (isCurrentFirst) {
				from = new Label("this");
				from.setStyleName("thisNodeLabel");
				to = new Label(rel.getTo().getName());
				to.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialog.hide();
						App.eventBus.fireEvent(new GraphEvent(rel.getTo()));
					}
				});
				to.setStyleName("viewNodeLink");
			} else {
				to = new Label("this");
				to.setStyleName("thisNodeLabel");
				from = new Label(rel.getFrom().getName());
				from.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						dialog.hide();
						App.eventBus.fireEvent(new GraphEvent(rel.getFrom()));
					}
				});
				from.setStyleName("viewNodeLink");
			}
			relationTable.setWidget(i, 0, from);
			relationTable.setWidget(i, 1, new Label(rel.getType().toString()));
			relationTable.setWidget(i, 2, to);
			i++;
		}
		dialog.center();
		dialog.show();
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public void setRelations(List<RelationMetadata> relations) {
		this.relations = relations;
	}

	@UiHandler({ "closeButton", "goLink" })
	void handleClick(ClickEvent event) {
		if (event.getSource() == goLink) {
			NavigationEvent navigationEvent = new NavigationEvent(currentNode.getUri(),
					currentNode.getNumPage() - 1);
			App.eventBus.fireEvent(navigationEvent);
		}
		dialog.hide();
	}
}
