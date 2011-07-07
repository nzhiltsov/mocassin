package ru.ksu.niimm.cll.mocassin.ui.viewer.client;

import java.util.EnumMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DocumentStructureGraphPanel extends CaptionPanel implements
		ClickHandler {
	@UiTemplate("DocumentStructureGraphPanel.ui.xml")
	interface Binder extends
			UiBinder<DocumentStructureGraphPanel, DocumentStructureGraphPanel> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private ViewerConstants constants = GWT.create(ViewerConstants.class);

	@UiField
	VerticalPanel graphPanel;
	@UiField
	CheckBox hasPartCheckbox;
	@UiField
	CheckBox refersToCheckbox;
	@UiField
	CheckBox dependsOnCheckbox;
	@UiField
	CheckBox provesCheckbox;
	@UiField
	CheckBox hasConsequenceCheckbox;
	@UiField
	CheckBox exemplifiesCheckbox;
	@UiField
	DocumentStructureGraph documentStructureGraph;

	enum Relations {
		hasPart, refersTo, dependsOn, proves, hasConsequence, exemplifies
	}

	public DocumentStructureGraphPanel() {
		uiBinder.createAndBindUi(this);
		setCaptionText(constants.graphPanelTitle());
	}

	public void refresh(ArticleInfo result) {
		EnumMap<Relations, Boolean> filters = new EnumMap<DocumentStructureGraphPanel.Relations, Boolean>(
				Relations.class);
		filters.put(Relations.hasPart, hasPartCheckbox.getValue());
		filters.put(Relations.refersTo, refersToCheckbox.getValue());
		filters.put(Relations.dependsOn, dependsOnCheckbox.getValue());
		filters.put(Relations.proves, provesCheckbox.getValue());
		filters.put(Relations.hasConsequence, hasConsequenceCheckbox.getValue());
		filters.put(Relations.exemplifies, exemplifiesCheckbox.getValue());
		documentStructureGraph.refresh(result, filters);
	}

	@UiHandler({ "hasPartCheckbox", "refersToCheckbox", "dependsOnCheckbox",
			"provesCheckbox", "hasConsequenceCheckbox", "exemplifiesCheckbox" })
	public void onClick(ClickEvent event) {

	}

}
