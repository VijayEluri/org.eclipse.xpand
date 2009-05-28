/*******************************************************************************
 * Copyright (c) 2005, 2009 committers of openArchitectureWare and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of openArchitectureWare - initial API and implementation
 *******************************************************************************/

package org.eclipse.xtend.shared.ui;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtend.expression.TypeSystem;
import org.eclipse.xtend.typesystem.MetaModel;

public interface MetamodelContributor {

    MetaModel[] getMetamodels(IJavaProject project, TypeSystem ctx);
    
}
