package ru.ksu.niimm.cll.mocassin.frontend.viewer.client;

import java.util.EnumMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;

public class DocumentStructureGraphPanel extends Composite implements
		ClickHandler {
	@UiTemplate("DocumentStructureGraphPanel.ui.xml")
	interface Binder extends
			UiBinder<CaptionPanel, DocumentStructureGraphPanel> {
	}

	private static Binder uiBinder = GWT.create(Binder.class);

	private ViewerConstants constants = GWT.create(ViewerConstants.class);

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
	@UiField
	Button refreshButton;

	public DocumentStructureGraphPanel() {
		CaptionPanel captionPanel = uiBinder.createAndBindUi(this);
		captionPanel.setCaptionText(constants.graphPanelTitle());
		initWidget(captionPanel);
	}

	public void refresh(ArticleInfo result) {
		EnumMap<Relations, Boolean> filters = getFilters();
		documentStructureGraph.refresh(result, filters);
	}

	private EnumMap<Relations, Boolean> getFilters() {
		EnumMap<Relations, Boolean> filters = new EnumMap<Relations, Boolean>(
				Relations.class);
		filters.put(Relations.hasPart, hasPartCheckbox.getValue());
		filters.put(Relations.refersTo, refersToCheckbox.getValue());
		filters.put(Relations.dependsOn, dependsOnCheckbox.getValue());
		filters.put(Relations.proves, provesCheckbox.getValue());
		filters.put(Relations.hasConsequence, hasConsequenceCheckbox.getValue());
		filters.put(Relations.exemplifies, exemplifiesCheckbox.getValue());
		return filters;
	}

	@UiHandler("refreshButton")
	public void onClick(ClickEvent event) {
		EnumMap<Relations, Boolean> filters = getFilters();
		documentStructureGraph.refresh(filters);
	}

}
