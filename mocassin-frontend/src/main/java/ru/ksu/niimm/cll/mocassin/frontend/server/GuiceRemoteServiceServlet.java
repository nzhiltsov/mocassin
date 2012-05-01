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
package ru.ksu.niimm.cll.mocassin.frontend.server;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@SuppressWarnings("serial")
@Singleton
public class GuiceRemoteServiceServlet extends RemoteServiceServlet {
	@Inject
	private Injector injector;

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			RPCRequest req = RPC.decodeRequest(payload, null, this);

			RemoteService service = getServiceInstance(req.getMethod()
					.getDeclaringClass());

			return RPC.invokeAndEncodeResponse(service, req.getMethod(), req
					.getParameters(), req.getSerializationPolicy());
		} catch (IncompatibleRemoteServiceException ex) {
			log(
					"IncompatibleRemoteServiceException in the processCall(String) method.",
					ex);
			return RPC.encodeResponseForFailure(null, ex);
		}
	}

	@SuppressWarnings( { "unchecked" })
	private RemoteService getServiceInstance(Class serviceClass) {
		return (RemoteService) injector.getInstance(serviceClass);
	}
}
