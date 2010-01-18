package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.button.SelectedSuggestionButton;
import ru.ksu.niimm.ose.ui.client.widget.flextable.CellCoordinate;
import ru.ksu.niimm.ose.ui.client.widget.flextable.SearchableFlexTable;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementOracle;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class TopPanel extends Composite {
	interface Binder extends UiBinder<VerticalPanel, TopPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private final OntologyServiceAsync ontologyService = GWT
			.create(OntologyService.class);

	private SearchableFlexTable table;
	private SelectedSuggestionButton bottomButton;

	private SelectedSuggestionButton rightButton;

	private OntologyElementOracle instanceOracle;

	private OntologyElementSuggestBox instanceSuggestBox;
	@UiField
	HorizontalPanel panel;
	@UiField
	Button sendButton;
	@UiField
	Button clearButton;

	public TopPanel() {
		initWidget(binder.createAndBindUi(this));
		setGrid(new SearchableFlexTable());
		panel.add(getGrid());
		// initializeGrid();
	}

	private void initializeGrid() {

		setRightButton(new SelectedSuggestionButton("+"));
		setBottomButton(new SelectedSuggestionButton("+"));
		getBottomButton().setVisible(false);
		instanceOracle = new OntologyElementOracle();
		instanceSuggestBox = new OntologyElementSuggestBox(instanceOracle);
		instanceSuggestBox.addSelectionHandler(getRightButton());
		instanceSuggestBox.addSelectionHandler(getBottomButton());

		getGrid().setWidget(0, 0, instanceSuggestBox);
		getGrid().setWidget(0, 1, rightButton);
		getGrid().setWidget(1, 1, bottomButton);

		loadConceptNamesList();
	}

	public SearchableFlexTable getGrid() {
		return table;
	}

	public void setGrid(SearchableFlexTable table) {
		this.table = table;
	}

	public SelectedSuggestionButton getBottomButton() {
		return bottomButton;
	}

	public void setBottomButton(SelectedSuggestionButton bottomButton) {
		this.bottomButton = bottomButton;
	}

	public SelectedSuggestionButton getRightButton() {
		return rightButton;
	}

	public void setRightButton(SelectedSuggestionButton rightButton) {
		this.rightButton = rightButton;
	}

	@UiHandler("clearButton")
	void handleClick(ClickEvent event) {
		getGrid().clear();
		initializeGrid();
	}

	private void loadConceptNamesList() {
		AsyncCallbackWrapper<List<OntConcept>> callback = new AsyncCallbackWrapper<List<OntConcept>>() {

			@Override
			public void handleSuccess(List<OntConcept> result) {
				instanceOracle.setOntologyElements(result);
				// instanceSuggestBox.showSuggestionList();
			}
		};
		callback.beforeCall();
		ontologyService.getConceptList(callback);
	}

	public void insertRightGridWidget(Widget beforeWidget, Widget addedWidget) {
		CellCoordinate beforeWidgetCoordinate = getGrid().search(beforeWidget);
		int rowNumber = beforeWidgetCoordinate.getRowNumber();
		int columnNumber = beforeWidgetCoordinate.getColumnNumber();
		getGrid().insertCell(rowNumber, columnNumber);
		getGrid().setWidget(rowNumber, columnNumber, addedWidget);
	}

	public CellCoordinate addRightGridWidget(Widget beforeWidget,
			Widget addedWidget) {
		CellCoordinate beforeWidgetCoordinate = getGrid().search(beforeWidget);
		int rowNumber = beforeWidgetCoordinate.getRowNumber();
		int columnNumber = beforeWidgetCoordinate.getColumnNumber();
		getGrid().insertCell(rowNumber, columnNumber + 1);
		getGrid().setWidget(rowNumber, columnNumber + 1, addedWidget);
		return new CellCoordinate(rowNumber, columnNumber + 1);
	}

	public void insertBottomGridWidget(Widget beforeWidget, Widget addedWidget) {
		CellCoordinate beforeWidgetCoordinate = getGrid().search(beforeWidget);
		int rowNumber = beforeWidgetCoordinate.getRowNumber();
		int columnNumber = beforeWidgetCoordinate.getColumnNumber();
		int newRowNumber = getGrid().insertRow(rowNumber);
		getGrid().setWidget(newRowNumber, columnNumber, addedWidget);
	}

}
