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
package ru.ksu.niimm.cll.mocassin.frontend.dashboard.client;

import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadConfig;
import ru.ksu.niimm.cll.mocassin.frontend.common.client.PagingLoadInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("GWT.rpc")
public interface ArxivService extends RemoteService {
	
	public PagingLoadInfo<ArxivArticleMetadata> loadArticles(PagingLoadConfig pagingLoadConfig);
}