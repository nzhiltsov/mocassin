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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class QueryTree extends Composite {

	private static QueryTreeUiBinder uiBinder = GWT
			.create(QueryTreeUiBinder.class);

	interface QueryTreeUiBinder extends UiBinder<VerticalPanel, QueryTree> {
	}

	@UiField
	Tree tree;

	public QueryTree() {
		initWidget(uiBinder.createAndBindUi(this));
		initializeRoot();
	}

	private void initializeRoot() {
		TreeItem root = new TreeItem();
		this.tree.addItem(root);
		root.setWidget(new ConceptTreeNode(root));
	}

	public void clearTree() {
		tree.clear();
		initializeRoot();
	}

	public TreeItem getRoot() {
		return tree.getItem(0);
	}

}
