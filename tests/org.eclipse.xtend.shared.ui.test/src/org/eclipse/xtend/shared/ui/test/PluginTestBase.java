/*******************************************************************************
 * Copyright (c) 2005, 2007 committers of openArchitectureWare and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     committers of openArchitectureWare - initial API and implementation
 *******************************************************************************/
package org.eclipse.xtend.shared.ui.test;

import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public abstract class PluginTestBase  extends TestCase {
    protected TestEnvironment env;

    @Override
	public void setUp() throws Exception {
        env = new TestEnvironment();
    }

    @Override
	public void tearDown() throws Exception {
        env.resetWorkspace();
    }
    
    /**
     * Asserts that no markers with severity ERROR are attached to a file
     * @param tpl Path to the file
     */
    public void assertNoErrorMarkers (final IPath tpl) {
        final IMarker[] markers = env.getMarkersFor(tpl);
        for (IMarker marker : markers) {
        	try {
        		if (marker.isSubtypeOf(IMarker.PROBLEM) && marker.getAttribute(IMarker.SEVERITY,-1)==IMarker.SEVERITY_ERROR) {
        			fail(getDescription(marker));
        		}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
        }
    }
    
    /**
     * Prints out the markers attached to a file
     * @param tpl Path to the file
     */
    protected void dumpMarkers(final IPath tpl) {
        final IMarker[] markers = env.getMarkersFor(tpl);
        for (int i = 0; i < markers.length; i++) {
            final IMarker marker = markers[i];
            System.out.println(getDescription(marker));
        }
    }
    
    /**
     * Gets a string representation of marker
     * @param marker The marker instance
     * @return Readable description of the marker
     */
	protected String getDescription (IMarker marker) {
    	try {
	    	HashMap<String, Object> map = new HashMap<String, Object>();
	    	map.put("resource",marker.getResource().getName());
	    	map.put("type", marker.getType());
	    	map.put("attributes", new HashMap<String,Object>(marker.getAttributes()).toString());
	    	return map.toString();
    	} catch (CoreException e) {
    		throw new RuntimeException(e);
    	}
    }

}
