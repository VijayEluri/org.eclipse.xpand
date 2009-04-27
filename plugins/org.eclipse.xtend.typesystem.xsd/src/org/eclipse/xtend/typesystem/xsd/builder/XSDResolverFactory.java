/*******************************************************************************
 * Copyright (c) 2005 - 2008 committers of openArchitectureWare and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of openArchitectureWare - initial API and implementation
 *******************************************************************************/
package org.eclipse.xtend.typesystem.xsd.builder;


import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class XSDResolverFactory extends AdapterFactoryImpl {

	protected XSDResolver resolver = new XSDResolver();

	public Adapter adaptNew(Notifier target, Object type) {
		return resolver;
	}

	public boolean isFactoryForType(Object type) {
		return resolver.isAdapterForType(type);
	}

}