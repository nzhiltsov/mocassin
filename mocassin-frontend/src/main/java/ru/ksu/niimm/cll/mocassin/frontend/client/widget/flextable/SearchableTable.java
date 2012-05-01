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
