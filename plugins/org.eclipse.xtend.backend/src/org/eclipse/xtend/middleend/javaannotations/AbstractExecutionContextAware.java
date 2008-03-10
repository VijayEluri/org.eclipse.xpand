/*
Copyright (c) 2008 Arno Haase.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
    Arno Haase - initial API and implementation
 */
package org.eclipse.xtend.middleend.javaannotations;

import org.eclipse.xtend.backend.common.ExecutionContext;


/**
 * This is a convenient default implementation of the marker interface ExecutionContextAware.
 *  
 * @author Arno Haase (http://www.haase-consulting.com)
 */
public abstract class AbstractExecutionContextAware implements ExecutionContextAware {
    protected ExecutionContext _ctx;
    
    public final void setExecutionContext (ExecutionContext ctx) {
        _ctx = ctx;
    }
    
    //TODO add "invoke" method - and rename this to AbstractJavaDefinedFunctions, with implementations for the import methods as well
    
    protected ExecutionContext getExecutionContext () {
        return _ctx;
    }
}
