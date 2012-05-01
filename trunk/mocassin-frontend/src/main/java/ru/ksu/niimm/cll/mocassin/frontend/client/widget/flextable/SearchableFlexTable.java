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
package ru.ksu.niimm.cll.mocassin.frontend.client.widget.flextable;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class SearchableFlexTable extends FlexTable implements SearchableTable {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ui.client.widget.flextable.SearchableTable#search(com
	 * .google.gwt.user.client.ui.Widget)
	 */
	public CellCoordinate search(Widget widget) {
		for (int i = 0; i < getRowCount(); i++)
			for (int j = 0; j < getCellCount(i); j++) {
				Widget cellWidget = getWidget(i, j);
				if (cellWidget != null && cellWidget.equals(widget)) {
					return new CellCoordinate(i, j);
				}
			}
		return null;
	}
}
