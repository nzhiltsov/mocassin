/*******************************************************************************
 * Copyright (c) 2010-2012 Nikita Zhiltsov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Nikita Zhiltsov - initial API and implementation
 *     Azat Khasanshin - implementation
 ******************************************************************************/
package ru.ksu.niimm.cll.mocassin.frontend.dashboard.client;

import java.util.ArrayList;
import java.util.List;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.AsyncCallbackWrapper;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PageLinkEvent;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PageLinkEventHandler;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PaginationPanel;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class DashboardTabPanel extends Composite implements
		PageLinkEventHandler {
	private static final PagingLoadConfig INITIAL_PAGING_LOAD_CONFIG = new PagingLoadConfig();
	{
		INITIAL_PAGING_LOAD_CONFIG.setLimit(10);
		INITIAL_PAGING_LOAD_CONFIG.setOffset(0);
	}
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
	PaginationPanel paginationPanel;

	@UiField
	FormPanel multipleUploadForm;

	public DashboardTabPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		tabPanel.selectTab(0);
		
		uploadButton.setEnabled(false);

		documentTable.getColumnFormatter().setWidth(0, "50px");
		documentTable.getColumnFormatter().setWidth(1, "300px");

		documentTable.setText(0, 0, "arXiv key");
		documentTable.setText(0, 1, "Title");

		multipleUploadForm
				.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						closeDialogButton.setEnabled(true);
						multipleUploadForm.reset();
						dialogBox.setText(event.getResults());
					}
				});

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				Integer selectedItem = event.getSelectedItem();
				if (selectedItem == 1) {
					loadArticles(INITIAL_PAGING_LOAD_CONFIG);
				}
			}

		});
		paginationPanel.addPageLinkEventHandler(this);
	}

	private void loadArticles(PagingLoadConfig pagingLoadConfig) {
		final AsyncCallbackWrapper<PagingLoadInfo<ArxivArticleMetadata>> callback = new AsyncCallbackWrapper<PagingLoadInfo<ArxivArticleMetadata>>() {
			@Override
			public void handleFailure(Throwable caught) {
				Window.alert("Loading papers has failed:" + caught.getMessage());
			}

			@Override
			public void handleSuccess(
					PagingLoadInfo<ArxivArticleMetadata> result) {
				documentTable.clear();
				List<ArxivArticleMetadata> data = new ArrayList<ArxivArticleMetadata>(
						result.getData());
				for (int i = 0; i < data.size(); i++) {
					documentTable.setText(i + 1, 0, data.get(i).getKey());
					documentTable.setText(i + 1, 1, data.get(i).getTitle());
				}
				paginationPanel.refresh(result);
			}
		};
		callback.beforeCall();
		arxivService.loadArticles(pagingLoadConfig, callback);
	}

	@Override
	public void handlePageLinkEvent(PageLinkEvent event) {
		loadArticles(event.getPagingLoadConfig());
	}

	@UiHandler({ "uploadButton", "closeDialogButton", "multipleUploadButton" })
	void handleClick(ClickEvent event) {
		
		if (event.getSource() == uploadButton) {
			// nothing
		} else if (event.getSource() == closeDialogButton) {
			dialogBox.hide();
		} else if (event.getSource() == multipleUploadButton) {
			closeDialogButton.setEnabled(false);
			dialogBox.setText("Uploading data. Please wait...");
			dialogBox.center();
			dialogBox.show();
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
