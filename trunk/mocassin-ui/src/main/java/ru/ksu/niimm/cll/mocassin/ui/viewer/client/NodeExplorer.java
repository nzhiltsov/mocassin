package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

public class NodeExplorer extends DialogBox {
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

	private Node currentNode;

	public NodeExplorer() {
		setWidget(uiBinder.createAndBindUi(this));
	}

	@Override
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
		super.show();
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	@UiHandler({ "closeButton", "goLink" })
	void handleClick(ClickEvent event) {
		if (event.getSource() == goLink) {
			NavigationEvent navigationEvent = new NavigationEvent(
					currentNode.getNumPage() - 1);
			App.eventBus.fireEvent(navigationEvent);
		}
		this.hide();
	}
}
