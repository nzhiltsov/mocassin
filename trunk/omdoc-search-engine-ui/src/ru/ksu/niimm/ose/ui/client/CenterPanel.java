package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ru.ksu.niimm.ose.ui.client.widget.flextable.SearchableFlexTable;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestBox;
import ru.ksu.niimm.ose.ui.client.widget.suggestbox.OntologyElementSuggestion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
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
	SearchResultsCountPanel searchResultsCountPanel;
	@UiField
	VerticalPanel resultsPanel;

	public CenterPanel() {
		initWidget(binder.createAndBindUi(this));
	}

	public void query(OntQueryStatement statement) {
		AsyncCallbackWrapper<List<ResultDescription>> callback = new AsyncCallbackWrapper<List<ResultDescription>>() {

			@Override
			public void handleSuccess(List<ResultDescription> result) {

				resultsPanel.clear();
				for (ResultDescription resultDescription : result) {
					resultsPanel.add(new HitDescription(resultDescription));
				}
				searchResultsCountPanel.setSize(result.size());
			}

		};
		callback.beforeCall();
		ontologyService.query(statement, callback);
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
			// TODO : fix it!!
			throw new UnsupportedOperationException("not implemented yet");
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
					OntElement selectedValue = suggestBox.getSelectedValue();
					ontElements.add(selectedValue);
				}
			}
			for (int k = 2; k < ontElements.size(); k += 2) {
				OntTriple triple = new OntTriple(ontElements.get(k - 2),
						ontElements.get(k - 1), ontElements.get(k));
				triples.add(triple);
			}
			Widget mainWidget = getGrid().getWidget(0, 0);
			OntElement mainConcept = ((OntologyElementSuggestBox) mainWidget)
					.getSelectedValue();
			for (int m = 1; m < getGrid().getRowCount() - 1; m++) {
				OntElement predicateValue = ((OntologyElementSuggestBox) getGrid()
						.getWidget(m, 1)).getSelectedValue();
				OntElement objectValue = ((OntologyElementSuggestBox) getGrid()
						.getWidget(m, 2)).getSelectedValue();
				OntTriple triple = new OntTriple(mainConcept, predicateValue,
						objectValue);
				triples.add(triple);
			}
			OntQueryStatement st = new OntQueryStatement(mainConcept, triples);
			getCenterPanel().query(st);
		}
	}

}
