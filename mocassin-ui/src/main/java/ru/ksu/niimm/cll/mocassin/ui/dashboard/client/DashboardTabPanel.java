package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import java.util.List;

import ru.ksu.niimm.cll.mocassin.ui.client.QueryService;
import ru.ksu.niimm.cll.mocassin.ui.client.QueryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DashboardTabPanel extends Composite {

	private static DashboardTabPanelUiBinder uiBinder = GWT
			.create(DashboardTabPanelUiBinder.class);

	interface DashboardTabPanelUiBinder extends
			UiBinder<Widget, DashboardTabPanel> {
	}

	private final ArxivServiceAsync arxivService = GWT
			.create(ArxivService.class);

	@UiField
	Button uploadButton;

	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	TextBox uploadKeyInput;
	@UiField
	Label successMessage;
	@UiField
	Label errorMessage;
	@UiField
	FlexTable documentTable;

	public DashboardTabPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		tabPanel.selectTab(0);
		successMessage.setVisible(false);
		errorMessage.setVisible(false);

		documentTable.getColumnFormatter().setWidth(0, "50px");
		documentTable.getColumnFormatter().setWidth(1, "300px");

		documentTable.setText(0, 0, "arXiv key");
		documentTable.setText(0, 1, "Title");

		AsyncCallback<List<ArxivArticleMetadata>> callback = new AsyncCallback<List<ArxivArticleMetadata>>() {

			@Override
			public void onSuccess(List<ArxivArticleMetadata> result) {
				for (int i = 0; i < result.size(); i++) {
					documentTable.setText(i + 1, 0, result.get(i).getKey());
					documentTable.setText(i + 1, 1, result.get(i).getTitle());
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("loading papers has failed:" + caught.getMessage());

			}
		};
		arxivService.loadArticles(callback);
	}

	@UiHandler("uploadButton")
	void handleClick(ClickEvent event) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				successMessage.setVisible(true);

			}

			@Override
			public void onFailure(Throwable caught) {
				errorMessage.setVisible(true);
			}
		};
		successMessage.setVisible(false);
		errorMessage.setVisible(false);
		arxivService.handle(uploadKeyInput.getValue(), callback);
	}
}
