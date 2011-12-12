/*
Copyright (c) 2008 Arno Haase, Andr� Arnold.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
    Arno Haase - initial API and implementation
    Andr� Arnold
 */
package org.eclipse.xtend.backend.syslib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.xtend.backend.common.BackendType;
import org.eclipse.xtend.backend.common.Function;
import org.eclipse.xtend.backend.common.Property;
import org.eclipse.xtend.backend.common.QualifiedName;
import org.eclipse.xtend.backend.common.StaticProperty;
import org.eclipse.xtend.backend.functions.SourceDefinedFunction;
import org.eclipse.xtend.middleend.javaannotations.AbstractExecutionContextAware;


/**
 * This class provides the built-in operations for the Xtend reflection layer
 * 
 * @author Arno Haase (http://www.haase-consulting.com)
 * @author Andr� Arnold
 */
public final class ReflectionOperations extends AbstractExecutionContextAware {

    //////////////////////////////////
    // operations on Type
    //////////////////////////////////
    
    public Object newInstance (BackendType t) {
        return t.create();
    }

    public boolean isInstance (BackendType t, Object o) {
        return t.isAssignableFrom (_ctx.getTypesystem().findType(o));
    }

    public boolean isAssignableFrom (BackendType t1, BackendType t2) {
        return t1.isAssignableFrom (t2);
    }

    public Property getProperty (BackendType t, String name) {
        return t.getProperties(_ctx).get (name);
    }
    
    public StaticProperty getStaticProperty (BackendType t, String name) {
        return t.getStaticProperties().get (name);
    }
    
    public Function getOperation (BackendType t, QualifiedName name, List<BackendType> paramTypes) {
        final List<BackendType> allParamTypes = new ArrayList<BackendType> ();
        allParamTypes.add (t);
        allParamTypes.addAll (paramTypes);
        return _ctx.getFunctionDefContext().getMatch (_ctx, name, allParamTypes);
    }
    
    public List<String> getOperationParamNames (Function f) {
    	//TODO throw an Exception instead?
    	final List<String> paramNames = new LinkedList<String> ();
    	return paramNames;
    }
    
    public List<String> getOperationParamNames (SourceDefinedFunction f) {
    	return f.getParamNames();
    }

    //////////////////////////////////
    // operations on Property
    //////////////////////////////////
    
    public Object get (Property p, Object o) {
        return p.get (_ctx, o);
    }
    
    public void set (Property p, Object o, Object value) {
        p.set (_ctx, o, value);
    }

    
    //////////////////////////////////
    // operations on StaticProperty
    //////////////////////////////////
    
    public Object get (StaticProperty p) {
        return p.get();
    }
}
