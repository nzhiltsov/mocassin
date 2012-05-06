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
package ru.ksu.niimm.cll.mocassin.rdf.ontology.util;

import java.io.Serializable;
import java.util.Comparator;

import ru.ksu.niimm.cll.mocassin.rdf.ontology.OntologyConcept;

@SuppressWarnings("serial")
public class OntologyConceptComparator implements Serializable, Comparator<OntologyConcept> {

	@Override
	public int compare(OntologyConcept o1, OntologyConcept o2) {
		return o1.getLabel().compareTo(o2.getLabel());
	}

}
