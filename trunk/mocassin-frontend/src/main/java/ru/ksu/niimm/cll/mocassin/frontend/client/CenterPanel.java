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
package ru.ksu.niimm.cll.mocassin.frontend.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.AsyncCallbackWrapper;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PageLinkEvent;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PageLinkEventHandler;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PaginationPanel;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CenterPanel extends Composite implements PageLinkEventHandler {
	interface Binder extends UiBinder<VerticalPanel, CenterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	private static final PagingLoadConfig INITIAL_PAGING_LOAD_CONFIG = new PagingLoadConfig();
	{
		INITIAL_PAGING_LOAD_CONFIG.setLimit(6);
		INITIAL_PAGING_LOAD_CONFIG.setOffset(0);
	}

	private final QueryServiceAsync queryService = GWT
			.create(QueryService.class);

	@UiField
	SearchResultsCountPanel searchResultsCountPanel;
	@UiField
	VerticalPanel resultsPanel;
	@UiField
	PaginationPanel paginationPanel;

	private OntQueryStatement statement;

	public CenterPanel() {
		initWidget(binder.createAndBindUi(this));
		searchResultsCountPanel.getResultTitleLabel().setVisible(false);
		paginationPanel.addPageLinkEventHandler(this);
	}

	@Override
	public void handlePageLinkEvent(PageLinkEvent event) {
		query(event.getPagingLoadConfig());
	}

	private void query() {
		query(INITIAL_PAGING_LOAD_CONFIG);
	}

	/**
	 * Important!! To query new statement need to call {@see
	 * CenterPanel#setStatement(OntQueryStatement)} before
	 * 
	 * query using pre-set statement {@see CenterPanel#statement}
	 */
	private void query(PagingLoadConfig pagingLoadConfig) {
		AsyncCallbackWrapper<PagingLoadInfo<ResultDescription>> callback = new AsyncCallbackWrapper<PagingLoadInfo<ResultDescription>>() {

			@Override
			public void handleSuccess(PagingLoadInfo<ResultDescription> result) {

				resultsPanel.clear();
				for (ResultDescription resultDescription : result.getData()) {
					resultsPanel.add(new HitDescription(resultDescription));
				}
				searchResultsCountPanel.getResultTitleLabel().setVisible(true);
				searchResultsCountPanel.setSize(result.getFullCollectionSize());
				paginationPanel.refresh(result);
			}

		};
		callback.beforeCall();
		queryService.query(statement, pagingLoadConfig, callback);

	}

	public OntQueryStatement getStatement() {
		return statement;
	}

	public void setStatement(OntQueryStatement statement) {
		this.statement = statement;
	}

	public static class BuildQueryStatementHandler implements ClickHandler {
		private CenterPanel centerPanel;
		private QueryTree tree;
		private InferenceCheckbox inferenceCheckbox;

		public BuildQueryStatementHandler(TopPanel panel,
				CenterPanel centerPanel) {
			this.tree = panel.tree;
			this.centerPanel = centerPanel;
			this.inferenceCheckbox = panel.inferenceCheckbox;
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

		public InferenceCheckbox getInferenceCheckbox() {
			return inferenceCheckbox;
		}

		public void setInferenceCheckbox(InferenceCheckbox inferenceCheckbox) {
			this.inferenceCheckbox = inferenceCheckbox;
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
						triples, getInferenceCheckbox().isChecked());
				getCenterPanel().setStatement(st);
				getCenterPanel().query();
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
					/**
					 * check if a query has only single subject
					 */
					if (i == 1 && item.getChildCount() == 0) {
						OntBlankNode predicate = new OntBlankNode();
						predicate.setId(2);
						OntBlankNode object = new OntBlankNode();
						object.setId(3);
						OntTriple triple = new OntTriple(selectedConcept,
								predicate, object);
						triples.add(triple);
					}
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

			} else if (item.getChildCount() == 0) {
				return new OntBlankNode();
			} else
				throw new RuntimeException("inconsistent state of query tree");
		}
	}

}
