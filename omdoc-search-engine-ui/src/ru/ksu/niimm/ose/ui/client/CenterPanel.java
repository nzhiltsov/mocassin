package ru.ksu.niimm.ose.ui.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CenterPanel extends Composite {
	interface Binder extends UiBinder<VerticalPanel, CenterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private final QueryServiceAsync queryService = GWT
			.create(QueryService.class);

	@UiField
	SearchResultsCountPanel searchResultsCountPanel;
	@UiField
	VerticalPanel resultsPanel;

	public CenterPanel() {
		initWidget(binder.createAndBindUi(this));
		searchResultsCountPanel.getResultTitleLabel().setVisible(false);
	}

	public void query(OntQueryStatement statement) {
		AsyncCallbackWrapper<List<ResultDescription>> callback = new AsyncCallbackWrapper<List<ResultDescription>>() {

			@Override
			public void handleSuccess(List<ResultDescription> result) {

				resultsPanel.clear();
				for (ResultDescription resultDescription : result) {
					resultsPanel.add(new HitDescription(resultDescription));
				}
				searchResultsCountPanel.getResultTitleLabel().setVisible(true);
				searchResultsCountPanel.setSize(result.size());
			}

		};
		callback.beforeCall();
		queryService.query(statement, callback);
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
				getCenterPanel().query(st);
			}

		}

		/**
		 * tree traversal in depth to build triples
		 * 
		 * @param node
		 * @return
		 */
		private List<OntTriple> buildQueryTriples(TreeItem node) {
			List<OntTriple> triples = new ArrayList<OntTriple>();

			Stack<TreeItem> queue = new Stack<TreeItem>();
			queue.push(node);
			/**
			 * Important !!! enumerate should start only with 1; see comment
			 * with 'id' field of OntElement
			 */
			int i = 1;
			while (!queue.isEmpty()) {
				TreeItem item = queue.pop();
				Widget itemWidget = item.getWidget();
				if (itemWidget instanceof PropertyTreeNode) {
					OntElement selectedProperty = ((PropertyTreeNode) itemWidget).suggestBoxPanel
							.getSelectedValue();
					selectedProperty.setId(i);
					OntElement selectedDomainConcept = getSelectedDomain(item);
					OntElement selectedRangeConcept = getSelectedRangeConcept(item);
					OntTriple triple = new OntTriple();
					triple.setPredicate(selectedProperty);
					triple.setSubject(selectedDomainConcept);
					triple.setObject(selectedRangeConcept);
					triples.add(triple);
				} else if (itemWidget instanceof ConceptTreeNode) {
					OntElement selectedConcept = ((ConceptTreeNode) itemWidget).suggestBoxPanel
							.getSelectedValue();
					selectedConcept.setId(i);
				} else
					throw new RuntimeException(
							"inconsistent state of query tree");
				for (int k = 0; k < item.getChildCount(); k++) {
					TreeItem child = item.getChild(k);
					queue.add(child);
				}
				i++;
			}
			return triples;
		}

		private OntElement getSelectedDomain(TreeItem item) {
			TreeItem parentItem = item.getParentItem();
			Widget parentItemWidget = parentItem.getWidget();
			if (parentItemWidget instanceof ConceptTreeNode) {
				OntElement selectedDomain = ((ConceptTreeNode) parentItemWidget).suggestBoxPanel
						.getSelectedValue();
				return selectedDomain;
			} else
				throw new RuntimeException("inconsistent state of query tree");
		}

		private OntElement getSelectedRangeConcept(TreeItem item) {
			if (item.getChildCount() == 1) {
				TreeItem childItem = item.getChild(0);
				Widget childItemWidget = childItem.getWidget();
				if (childItemWidget instanceof ConceptTreeNode) {
					OntElement selectedRange = ((ConceptTreeNode) childItemWidget).suggestBoxPanel
							.getSelectedValue();
					return selectedRange;
				} else
					throw new RuntimeException(
							"inconsistent state of query tree");

			} else
				throw new RuntimeException("inconsistent state of query tree");
		}
	}

}
