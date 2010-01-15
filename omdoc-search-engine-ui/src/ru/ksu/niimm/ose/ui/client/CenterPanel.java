package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.flextable.SearchableFlexTable;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestion;

import com.gargoylesoftware.htmlunit.javascript.host.Popup;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Callback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class CenterPanel extends Composite {
	interface Binder extends UiBinder<VerticalPanel, CenterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private final OntologyServiceAsync ontologyService = GWT
			.create(OntologyService.class);
	@UiField
	Label resultsLabel;

	@UiField
	VerticalPanel resultsPanel;

	LoadingPanel loadingPanel;

	public CenterPanel() {
		initWidget(binder.createAndBindUi(this));
		loadingPanel = new LoadingPanel();
	}

	public void query(OntQueryStatement statement) {
		AsyncCallback<List<ResultDescription>> callback = new AsyncCallback<List<ResultDescription>>() {

			@Override
			public void onSuccess(List<ResultDescription> result) {

				resultsPanel.clear();
				for (ResultDescription resultDescription : result) {
					resultsPanel.add(new HitDescription(resultDescription));
				}
				CenterPanel.this.loadingPanel.popupHide();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error while handling query:"
						+ caught.getMessage());
			}
		};
		ontologyService.query(statement, callback);
		loadingPanel.popupShow();
	}

	public static class BuildQueryStatementHandler implements ClickHandler {
		private TopPanel module;
		private CenterPanel centerPanel;

		public BuildQueryStatementHandler(TopPanel module,
				CenterPanel centerPanel) {
			this.module = module;
			this.centerPanel = centerPanel;
		}

		public CenterPanel getCenterPanel() {
			return centerPanel;
		}

		public void setCenterPanel(CenterPanel centerPanel) {
			this.centerPanel = centerPanel;
		}

		public SearchableFlexTable getGrid() {
			return module.getGrid();
		}

		public TopPanel getModule() {
			return module;
		}

		public void setModule(TopPanel module) {
			this.module = module;
		}

		@Override
		public void onClick(ClickEvent event) {
			// TODO : this is stub algorithm implementation; need tree traverse
			// algorithm
			List<OntTriple> triples = new ArrayList<OntTriple>();
			int i = 0;
			List<OntElement> ontElements = new ArrayList<OntElement>();
			for (int j = 0; j < getGrid().getCellCount(i); j++) {
				Widget cellWidget = getGrid().getWidget(i, j);
				if (cellWidget instanceof OntologyElementSuggestBox) {
					OntologyElementSuggestBox suggestBox = (OntologyElementSuggestBox) cellWidget;
					OntElement selectedValue = getSelectedValue(suggestBox);
					ontElements.add(selectedValue);
				}
			}
			for (int k = 2; k < ontElements.size(); k += 2) {
				OntTriple triple = new OntTriple(ontElements.get(k - 2),
						ontElements.get(k - 1), ontElements.get(k));
				triples.add(triple);
			}
			Widget mainWidget = getGrid().getWidget(0, 0);
			OntElement mainConcept = getSelectedValue((OntologyElementSuggestBox) mainWidget);
			for (int m = 1; m < getGrid().getRowCount() - 1; m++) {
				OntElement predicateValue = getSelectedValue((OntologyElementSuggestBox) getGrid()
						.getWidget(m, 1));
				OntElement objectValue = getSelectedValue((OntologyElementSuggestBox) getGrid()
						.getWidget(m, 2));
				OntTriple triple = new OntTriple(mainConcept, predicateValue,
						objectValue);
				triples.add(triple);
			}
			OntQueryStatement st = new OntQueryStatement(mainConcept, triples);
			getCenterPanel().query(st);
		}

		private OntElement getSelectedValue(OntologyElementSuggestBox suggestBox) {
			final String currentValue = suggestBox.getValue();
			Request request = new Request();
			SuggestionCallback callback = new SuggestionCallback(currentValue);
			suggestBox.getSuggestOracle().requestDefaultSuggestions(request,
					callback);
			return callback.getValue();
		}
	}

	public static class SuggestionCallback implements Callback {
		private String suggestionString;
		private OntElement value;

		public SuggestionCallback(String suggestionString) {
			this.suggestionString = suggestionString;
		}

		public OntElement getValue() {
			return value;
		}

		public void setValue(OntElement value) {
			this.value = value;
		}

		public String getSuggestionString() {
			return suggestionString;
		}

		public void setSuggestionString(String suggestionString) {
			this.suggestionString = suggestionString;
		}

		@Override
		public void onSuggestionsReady(Request request, Response response) {
			Collection<? extends Suggestion> suggestions = response
					.getSuggestions();
			boolean elementFound = false;
			for (Suggestion suggestion : suggestions) {
				String suggestionString = suggestion.getReplacementString();
				if (suggestion instanceof OntologyElementSuggestion
						&& suggestionString.equals(getSuggestionString())) {
					OntologyElementSuggestion ontSuggestion = (OntologyElementSuggestion) suggestion;
					setValue(ontSuggestion.getOntologyElement());
					elementFound = true;
					break;
				}
			}
			if (!elementFound) { // the value is individual (instance) of
				// ontology element
				setValue(new OntIndividual(getSuggestionString(),
						getSuggestionString()));
			}
		}
	}
}
