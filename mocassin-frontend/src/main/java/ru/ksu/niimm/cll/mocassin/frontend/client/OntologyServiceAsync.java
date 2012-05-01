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

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>OntologyService</code>.
 */
public interface OntologyServiceAsync {

	void getConceptList(AsyncCallback<List<OntConcept>> callback);

	void getRelationList(OntConcept concept,
			AsyncCallback<List<OntRelation>> callback);

	void getRelationRangeConceptList(OntRelation relation,
			AsyncCallback<List<OntElement>> callback);
}
