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

package org.eclipse.xtend.shared.ui.expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.mwe.core.monitor.ProgressMonitor;
import org.eclipse.internal.xpand2.pr.ProtectedRegionResolver;
import org.eclipse.internal.xtend.expression.ast.SyntaxElement;
import org.eclipse.internal.xtend.util.Pair;
import org.eclipse.internal.xtend.util.Triplet;
import org.eclipse.internal.xtend.xtend.ast.Extension;
import org.eclipse.xpand2.output.Output;
import org.eclipse.xtend.expression.ExceptionHandler;
import org.eclipse.xtend.expression.ExecutionContext;
import org.eclipse.xtend.expression.Resource;
import org.eclipse.xtend.expression.ResourceManager;
import org.eclipse.xtend.expression.ResourceParser;
import org.eclipse.xtend.expression.TypeSystemImpl;
import org.eclipse.xtend.expression.Variable;
import org.eclipse.xtend.shared.ui.core.IXtendXpandProject;
import org.eclipse.xtend.shared.ui.core.internal.BuildState;
import org.eclipse.xtend.typesystem.Type;

public class XpandPluginExecutionContext extends org.eclipse.xpand2.XpandExecutionContextImpl {
	private final IXtendXpandProject project;

	public XpandPluginExecutionContext(final IXtendXpandProject xp) {
		this(new PluginResourceManager(xp), null, new TypeSystemImpl(), new HashMap<String, Variable>(),
				new HashMap<String, Variable>(), null, null, null, xp, null);
	}

	protected XpandPluginExecutionContext(ResourceManager resourceManager, Resource currentResource,
			TypeSystemImpl typeSystem, Map<String, Variable> vars, Map<String, Variable> globalVars, Output output,
			ProtectedRegionResolver prs, ProgressMonitor monitor, IXtendXpandProject xp,
			Map<Pair<String, List<Type>>, Type> extensionsReturnTypeCache) {
		// to solve bug#312571: added DevNullExceptionHandler Without an ExceptionHandler RuntimeExceptions will cause an abort of analyzing Expressions. 
		super(resourceManager, currentResource, typeSystem, vars, globalVars, output, prs, monitor, new DevNullExceptionHandler(), null, null,
				null, null, null, extensionsReturnTypeCache, null);
		this.project = xp;
	}

	@Override
	public XpandPluginExecutionContext cloneContext() {
		return new XpandPluginExecutionContext(resourceManager, currentResource(), typeSystem, getVisibleVariables(),
				getGlobalVariables(), output, protectedRegionResolver, getMonitor(), project, extensionsReturnTypeCache);
	}

	public static class PluginResourceManager implements ResourceManager {
		private IXtendXpandProject project;

		public PluginResourceManager(final IXtendXpandProject project) {
            assert project!=null;
			this.project = project;
		}

		public Resource loadResource(final String fullyQualifiedName, final String extension) {
			return project.findExtXptResource(fullyQualifiedName, extension);
		}

		public void setFileEncoding(final String fileEncoding) {
		}

		public void registerParser(final String template_extension, final ResourceParser parser) {
		}
	}

	@Override
	public Set<? extends Extension> getAllExtensions() {
		BuildState buildState = BuildState.get(this);
		if (buildState!=null) {
			Set<? extends Extension> allExtensions = buildState.getAllExtensionsPerResource().get(currentResource());
			if (allExtensions==null) {
				allExtensions = super.getAllExtensions();
				buildState.getAllExtensionsPerResource().put(currentResource(), (Set<Extension>) allExtensions);
			}
			return allExtensions;
		} else {
			return super.getAllExtensions();
		}
	}
	
	@Override
	public Extension getExtensionForTypes(String functionName, Type[] parameterTypes) {
		BuildState buildState = BuildState.get(this);
		if (buildState!=null) {
			Triplet<Resource, String, List<Type>> key = new Triplet<Resource, String, List<Type>>(currentResource(),functionName,Arrays.asList(parameterTypes));
			Extension result = buildState.getExtensionForTypes().get(key);
			if (result == null) {
				result = super.getExtensionForTypes(functionName, parameterTypes);
				buildState.getExtensionForTypes().put(key,result);
			}
			return result;
		} else {
			return super.getExtensionForTypes(functionName, parameterTypes);
		}
	}
	
	/**
	 * 
	 * @author Benedikt Niehues - Initial contribution and API
	 * this ExceptionHandler will do nothing with exceptions.
	 * 
	 */
	private final static class DevNullExceptionHandler implements ExceptionHandler{

		public void handleRuntimeException(RuntimeException ex, SyntaxElement element, ExecutionContext ctx,
				Map<String, Object> additionalContextInfo) {
			// Do nothing
		}
		
	}
}
