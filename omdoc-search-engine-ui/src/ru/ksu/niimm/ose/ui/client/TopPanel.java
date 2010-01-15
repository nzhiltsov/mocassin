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

public class TopPanel extends Composite implements
		SelectionHandler<Suggestion>, ClickHandler {
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
		initializeGrid();
	}

	private void initializeGrid() {

		setRightButton(new SelectedSuggestionButton("+"));
		setBottomButton(new SelectedSuggestionButton("+"));
		getBottomButton().setVisible(false);
		instanceOracle = new OntologyElementOracle();
		instanceSuggestBox = new OntologyElementSuggestBox(instanceOracle);
		instanceSuggestBox.addSelectionHandler(this);
		instanceSuggestBox.addSelectionHandler(getRightButton());
		instanceSuggestBox.addSelectionHandler(getBottomButton());

		getGrid().setWidget(0, 0, instanceSuggestBox);
		getGrid().setWidget(0, 1, rightButton);
		getGrid().setWidget(1, 1, bottomButton);
		getRightButton().addClickHandler(this);
		getBottomButton().addClickHandler(this);

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

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source instanceof Button) {
			Button button = (Button) source;
			if (button.equals(getBottomButton())) {
				Suggestion selectedItem = getBottomButton().getSelectedItem();
				if (selectedItem instanceof OntologyElementSuggestion) {
					OntologyElementSuggestion selectedSuggestionItem = (OntologyElementSuggestion) selectedItem;
					loadPropertyList(selectedSuggestionItem
							.getOntologyElement(), true);
				}

			} else if (button.equals(getRightButton())) {
				Suggestion selectedItem = getRightButton().getSelectedItem();
				if (selectedItem instanceof OntologyElementSuggestion) {
					OntologyElementSuggestion selectedSuggestionItem = (OntologyElementSuggestion) selectedItem;
					loadPropertyList(selectedSuggestionItem
							.getOntologyElement(), false);
				}
			}
		}

	}

	@Override
	public void onSelection(SelectionEvent<Suggestion> event) {
		Object source = event.getSource();
		if (source instanceof OntologyElementSuggestBox) {
			OntologyElementSuggestBox suggestBox = (OntologyElementSuggestBox) source;
			Suggestion selectedItem = event.getSelectedItem();
			if (selectedItem instanceof OntologyElementSuggestion) {
				OntologyElementSuggestion ontologyElementSuggestion = (OntologyElementSuggestion) selectedItem;
				OntElement ontologyElement = ontologyElementSuggestion
						.getOntologyElement();
				if (ontologyElement instanceof OntRelation) {
					OntRelation selectedRelation = (OntRelation) ontologyElement;
					loadRangeConceptList(selectedRelation, suggestBox);
				}
			}
		}
	}

	private void loadRangeConceptList(OntRelation selectedRelation,
			final OntologyElementSuggestBox beforeSuggestBox) {
		AsyncCallback<List<OntElement>> callback = new AsyncCallback<List<OntElement>>() {

			@Override
			public void onSuccess(List<OntElement> result) {
				OntologyElementOracle rangeConceptOracle = new OntologyElementOracle();
				rangeConceptOracle.setOntologyElements(result);
				OntologyElementSuggestBox rangeConceptSuggestBox = new OntologyElementSuggestBox(
						rangeConceptOracle);
				rangeConceptSuggestBox.addSelectionHandler(TopPanel.this);

				CellCoordinate addedSuggestBoxCoordinate = addRightGridWidget(
						beforeSuggestBox, rangeConceptSuggestBox);
				CellCoordinate rightButtonCoordinate = getGrid().search(
						getRightButton());
				boolean isRightButtonNear = addedSuggestBoxCoordinate
						.getRowNumber() == rightButtonCoordinate.getRowNumber()
						&& addedSuggestBoxCoordinate.getColumnNumber() + 1 == rightButtonCoordinate
								.getColumnNumber();
				if (isRightButtonNear) {
					rangeConceptSuggestBox
							.addSelectionHandler(getRightButton());
				}

				getBottomButton().setVisible(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error while getting range concept list:"
						+ caught.getMessage());
			}
		};
		ontologyService.getRelationRangeConceptList(selectedRelation, callback);
	}

	private void loadConceptNamesList() {
		AsyncCallback<List<OntConcept>> callback = new AsyncCallback<List<OntConcept>>() {

			@Override
			public void onSuccess(List<OntConcept> result) {
				instanceOracle.setOntologyElements(result);
				// instanceSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error while getting concept names list:"
						+ caught.getMessage());
			}
		};
		ontologyService.getConceptList(callback);
	}

	private void loadPropertyList(OntElement selectedOntologyElement,
			final boolean isBottomAdd) {
		AsyncCallback<List<OntRelation>> callback = new AsyncCallback<List<OntRelation>>() {

			@Override
			public void onSuccess(List<OntRelation> result) {
				OntologyElementOracle propertyOracle = new OntologyElementOracle();
				propertyOracle.setOntologyElements(result);
				OntologyElementSuggestBox propertySuggestBox = new OntologyElementSuggestBox(
						propertyOracle);
				propertySuggestBox.addSelectionHandler(TopPanel.this);

				if (isBottomAdd) {
					insertBottomGridWidget(getBottomButton(),
							propertySuggestBox);
				} else {
					insertRightGridWidget(getRightButton(), propertySuggestBox);
				}

				propertySuggestBox.setFocus(true);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error while getting relations list:"
						+ caught.getMessage());

			}
		};
		if (selectedOntologyElement instanceof OntConcept) {
			OntConcept selectedConcept = (OntConcept) selectedOntologyElement;
			ontologyService.getRelationList(selectedConcept, callback);
		}

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
