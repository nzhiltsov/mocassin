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
package ru.ksu.niimm.cll.mocassin.util.inject.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.MembersInjector;

public class Slf4jMembersInjector<T>  implements MembersInjector<T> {
	private final Field field;
    private final Logger logger;

    Slf4jMembersInjector(Field aField) {
    	field = aField;
    	logger = LoggerFactory.getLogger(field.getDeclaringClass());
    	field.setAccessible(true);
    }

	public void injectMembers(T anArg0) {
		try {
			field.set(anArg0, logger);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
