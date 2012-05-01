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
package ru.ksu.niimm.cll.mocassin.frontend.common.client;

public interface IAsyncCallbackWrapper<T> {
	/**
	 * handle failed RPC call
	 * 
	 * @param caught
	 */
	void handleFailure(Throwable caught);

	/**
	 * handle successful RPC call
	 * 
	 * @param result
	 */
	void handleSuccess(T result);

	/**
	 * Important!!! Should always call this method before RPC call
	 */
	void beforeCall();

}
