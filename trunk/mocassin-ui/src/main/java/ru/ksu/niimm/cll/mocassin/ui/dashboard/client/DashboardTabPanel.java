package ru.ksu.niimm.cll.mocassin.ui.dashboard.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

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
	Button closeDialogButton;
	@UiField
	Button multipleUploadButton;

	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	TextBox uploadKeyInput;
	@UiField
	DialogBox dialogBox;
	@UiField
	FlexTable documentTable;

	@UiField
	FormPanel multipleUploadForm;

	public DashboardTabPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		tabPanel.selectTab(0);

		documentTable.getColumnFormatter().setWidth(0, "50px");
		documentTable.getColumnFormatter().setWidth(1, "300px");

		documentTable.setText(0, 0, "arXiv key");
		documentTable.setText(0, 1, "Title");

		multipleUploadForm
				.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						
						String body = event
								.getResults();
						JSONObject response = (JSONObject) JSONParser.parseLenient(body);
						double numberOfSuccesses = ((JSONNumber) response.get("numberOfSuccesses")).doubleValue();
						showSuccess((int) numberOfSuccesses);
						multipleUploadForm.reset();
					}
				});

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

	@UiHandler({ "uploadButton", "closeDialogButton", "multipleUploadButton" })
	void handleClick(ClickEvent event) {
		AsyncCallback<Void> singleUploadCallback = new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				showSuccess(1);

			}

			@Override
			public void onFailure(Throwable caught) {
				dialogBox.setText("The upload has failed.");
				dialogBox.center();
				dialogBox.show();
			}
		};
		if (event.getSource() == uploadButton) {
			arxivService.handle(uploadKeyInput.getValue(), singleUploadCallback);
		} else if (event.getSource() == closeDialogButton) {
			dialogBox.hide();
		} else if (event.getSource() == multipleUploadButton) {
			multipleUploadForm.submit();
		}
	}

	private void showSuccess(int number) {
		String message = number > 1 ? number
				+ " papers have been uploaded successfully!"
				: "The paper has been uploaded successfully!";
		dialogBox.setText(message);
		dialogBox.center();
		dialogBox.show();
	}
}
