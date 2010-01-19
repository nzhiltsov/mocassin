package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

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
import com.google.gwt.user.client.ui.TreeItem;
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
		private CenterPanel centerPanel;
		private QueryTree tree;

		public BuildQueryStatementHandler(QueryTree tree,
				CenterPanel centerPanel) {
			this.tree = tree;
			this.centerPanel = centerPanel;
		}

		public CenterPanel getCenterPanel() {
			return centerPanel;
		}

		public void setCenterPanel(CenterPanel centerPanel) {
			this.centerPanel = centerPanel;
		}

		public QueryTree getTree() {
			return tree;
		}

		public void setTree(QueryTree tree) {
			this.tree = tree;
		}

		@Override
		public void onClick(ClickEvent event) {
			TreeItem root = tree.getRoot();
			Widget rootItemWidget = root.getWidget();
			if (rootItemWidget instanceof ConceptTreeNode) {
				ConceptTreeNode rootConceptNode = (ConceptTreeNode) rootItemWidget;
				OntElement conceptNode = rootConceptNode.suggestBoxPanel
						.getSelectedValue();
				List<OntTriple> triples = buildQueryTriples(root);
				OntQueryStatement st = new OntQueryStatement(conceptNode,
						triples);
			}

		}

		/**
		 * tree traversal in depth to build triples
		 * 
		 * @param node
		 * @return
		 */
		private List<OntTriple> buildQueryTriples(TreeItem node) {
			// TODO : complete implementation !!!
			List<OntTriple> triples = new ArrayList<OntTriple>();
			OntTriple firstCurrentTriple = new OntTriple();
			triples.add(firstCurrentTriple);
			OntTriple secondCurrentTriple = new OntTriple();
			triples.add(secondCurrentTriple);

			Stack<TreeItem> stack = new Stack<TreeItem>();
			stack.push(node);
			while (!stack.isEmpty()) {
				TreeItem item = stack.pop();
				Widget itemWidget = item.getWidget();
				if (itemWidget instanceof ConceptTreeNode) {
					OntElement selectedConcept = ((ConceptTreeNode) itemWidget).suggestBoxPanel
							.getSelectedValue();
					firstCurrentTriple.setObject(selectedConcept);
					secondCurrentTriple.setSubject(selectedConcept);
				} else if (itemWidget instanceof PropertyTreeNode) {
					OntElement selectedProperty = ((PropertyTreeNode) itemWidget).suggestBoxPanel
							.getSelectedValue();
					secondCurrentTriple.setPredicate(selectedProperty);
				} else
					throw new RuntimeException(
							"inconsistent state of query tree");

				for (int k = 0; k < item.getChildCount(); k++) {
					TreeItem child = item.getChild(k);
					stack.add(child);
				}
			}
			return triples;
		}
	}

}
