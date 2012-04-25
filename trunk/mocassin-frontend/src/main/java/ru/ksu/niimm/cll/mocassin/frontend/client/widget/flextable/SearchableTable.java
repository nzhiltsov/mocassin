package ru.ksu.niimm.cll.mocassin.frontend.client.widget.flextable;

import com.google.gwt.user.client.ui.Widget;

public interface SearchableTable {
	/**
	 * receive cell coordinates for given widget of table
	 * 
	 * @param widget
	 * @return found widget or null if table doesn't contain given widget
	 */
	CellCoordinate search(Widget widget);

}