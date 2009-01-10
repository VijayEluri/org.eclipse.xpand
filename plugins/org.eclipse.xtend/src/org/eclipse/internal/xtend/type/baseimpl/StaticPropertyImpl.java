/*******************************************************************************
 * Copyright (c) 2005-2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package org.eclipse.internal.xtend.type.baseimpl;

import org.eclipse.xtend.typesystem.StaticProperty;
import org.eclipse.xtend.typesystem.Type;

public abstract class StaticPropertyImpl extends FeatureImpl implements StaticProperty {

    private Type owner;

    public StaticPropertyImpl(final Type owner, final String name, final Type returnType) {
        super(name, returnType);
        this.owner = owner;
    }

    public Type getOwner() {
        return owner;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof StaticProperty) {
            final StaticProperty op = (StaticProperty) obj;
            return getOwner().equals(op.getOwner()) && getName().equals(op.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append(getReturnType().toString());
        buff.append(" ").append(getOwner().toString());
        buff.append("#").append(getName());
        return buff.toString();
    }

    public String getDocumentation() {
        return "";
    }
}
