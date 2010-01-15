package ru.ksu.niimm.ose.ui.client.widget.flextable;

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
