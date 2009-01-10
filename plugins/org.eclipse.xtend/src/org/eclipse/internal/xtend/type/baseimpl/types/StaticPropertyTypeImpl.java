/*******************************************************************************
 * Copyright (c) 2005-2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package org.eclipse.internal.xtend.type.baseimpl.types;

import java.util.Collections;
import java.util.Set;

import org.eclipse.internal.xtend.type.baseimpl.OperationImpl;
import org.eclipse.xtend.expression.TypeSystem;
import org.eclipse.xtend.typesystem.Feature;
import org.eclipse.xtend.typesystem.StaticProperty;
import org.eclipse.xtend.typesystem.Type;

public final class StaticPropertyTypeImpl extends BuiltinBaseType {

    public StaticPropertyTypeImpl(final TypeSystem ts, final String name) {
        super(ts, name);
    }

    public boolean isInstance(final Object o) {
        return o instanceof StaticProperty;
    }

    public Object newInstance() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public Set<Type> getSuperTypes() {
        return Collections.singleton(getTypeSystem().getFeatureType());
    }

    @Override
    public Feature[] getContributedFeatures() {
        return new Feature[] { new OperationImpl(this, "get", getTypeSystem().getObjectType(), new Type[] {}) {

            @Override
            public String getDocumentation() {
                return "returns the static value";
            }

            @Override
            public Object evaluateInternal(final Object target, final Object[] params) {
                return ((StaticProperty) target).get();
            }
        } };
    }
}
