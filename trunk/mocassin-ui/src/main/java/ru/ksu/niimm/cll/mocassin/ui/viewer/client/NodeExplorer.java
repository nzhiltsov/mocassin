package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

public class NodeExplorer extends DialogBox {
	@UiTemplate("NodeExplorer.ui.xml")
	interface Binder extends UiBinder<NodeExplorer, NodeExplorer> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);
	
	@UiField
    Button closeButton;


	public NodeExplorer() {
		uiBinder.createAndBindUi(this);
	}

}
