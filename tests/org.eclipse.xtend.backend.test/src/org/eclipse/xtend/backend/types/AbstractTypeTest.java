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
package org.eclipse.xtend.backend.types;

import static org.eclipse.xtend.backend.testhelpers.BackendTestHelper.SOURCE_POS;
import static org.eclipse.xtend.backend.testhelpers.BackendTestHelper.createEmptyExecutionContext;
import static org.eclipse.xtend.backend.testhelpers.BackendTestHelper.createLiteral;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.eclipse.xtend.backend.expr.PropertyOnObjectExpression;
import org.junit.Test;


/**
 * 
 * @author Arno Haase (http://www.haase-consulting.com)
 * @author Andr� Arnold
 */
public class AbstractTypeTest {
    @Test public void testInheritedProperties () {
        // tests that ListType inherits the property "size" from CollectionType
        assertEquals (2L, new PropertyOnObjectExpression (createLiteral (Arrays.asList ("a", "b")), "size", SOURCE_POS).evaluate (createEmptyExecutionContext()));
    }
}
