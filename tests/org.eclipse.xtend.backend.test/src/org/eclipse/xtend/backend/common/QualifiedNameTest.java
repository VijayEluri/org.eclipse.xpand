/*
Copyright (c) 2008 Andr� Arnold.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
    Andr� Arnold - initial API and implementation
 */
package org.eclipse.xtend.backend.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * 
 * @author Andr� Arnold
 *
 */
public class QualifiedNameTest {
	
	private final String _fqn = "org::eclipse::xtend::backend::test::someName";
	private final String _simpleName = "someName";

	@Test
	public void testFqn() {
		QualifiedName name = new QualifiedName(_fqn);

		assertEquals("someName", name.getSimpleName());
		assertEquals("org::eclipse::xtend::backend::test", name.getNameSpace());
		assertEquals(_fqn, name.getFullQualifiedName());
		
	}

	@Test
	public void testSimpleName() {
		QualifiedName name = new QualifiedName(_simpleName);
		
		assertEquals("someName", name.getSimpleName());
		assertNull(name.getNameSpace());
		assertEquals(_simpleName, name.getFullQualifiedName());
		
	}
}
