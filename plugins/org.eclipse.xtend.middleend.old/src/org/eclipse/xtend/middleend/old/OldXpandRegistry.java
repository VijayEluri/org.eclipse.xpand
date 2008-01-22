/*
Copyright (c) 2008 Arno Haase.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
    Arno Haase - initial API and implementation
 */
package org.eclipse.xtend.middleend.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipose.xtend.middleend.FunctionDefContextFactory;
import org.eclipse.internal.xpand2.ast.Template;
import org.eclipse.internal.xpand2.model.XpandDefinition;
import org.eclipse.xpand2.XpandExecutionContext;
import org.eclipse.xpand2.XpandUtil;
import org.eclipse.xtend.backend.common.NamedFunction;
import org.eclipse.xtend.backend.functions.FunctionDefContextImpl;
import org.eclipse.xtend.backend.types.CompositeTypesystem;
import org.eclipse.xtend.backend.util.Cache;


/**
 * This class manages the interdependent graph of parsed and converted Xpand files, allowing access to them by "compilation unit".
 * 
 * @author Arno Haase (http://www.haase-consulting.com)
 */
final class OldXpandRegistry {
    private final XpandExecutionContext _ctx;
    private final CompositeTypesystem _ts;
    private final OldXtendRegistry _extensions;

    private final Cache<String, FunctionDefContextImpl> _functionDefContexts = new Cache<String, FunctionDefContextImpl> () {
        @Override
        protected FunctionDefContextImpl create (String compilationUnit) {
            return new FunctionDefContextFactory (_ts).create();
        }
    };
    
    /**
     * all functions actually defined in a given compilation unit
     */
    private final Map<String, List<NamedFunction>> _functionsByResource = new HashMap <String, List<NamedFunction>>();
    

    public OldXpandRegistry (XpandExecutionContext ctx, CompositeTypesystem ts, OldXtendRegistry extensions) {
        _ctx = ctx;
        _ts = ts;
        _extensions = extensions;
    }
    
    
    private FunctionDefContextImpl getFunctionDefContext (String xtendName) {
        return _functionDefContexts.get (OldXtendHelper.normalizeXtendResourceName (xtendName));
    }
    
    
    /**
     * parses and converts an Xpand file and all other files it depends on.
     */
    public void registerXpandFile (String xpandFile) {
        xpandFile = OldXtendHelper.normalizeXpandResourceName (xpandFile);
        
        if (_functionsByResource.containsKey (xpandFile))
            return;
        
        final Template file = (Template) _ctx.getResourceManager().loadResource (xpandFile, XpandUtil.TEMPLATE_EXTENSION);
        if (file == null)
            throw new IllegalArgumentException ("could not find extension '" + xpandFile + "'");
        
        final XpandExecutionContext ctx = (XpandExecutionContext) _ctx.cloneWithResource (file);
        
        final TypeToBackendType typeConverter = new TypeToBackendType (_ts, ctx);
        final OldDefinitionConverter definitionFactory = new OldDefinitionConverter (ctx, typeConverter);
        
        final List<NamedFunction> defined = new ArrayList<NamedFunction>();
        final FunctionDefContextImpl fdc = getFunctionDefContext (xpandFile);
        
        //TODO imported namespaces
        //TODO referenced other template files
        
        for (XpandDefinition ext: file.getDefinitions ())
            defined.add (definitionFactory.create (ext, fdc));
        
        
        _functionsByResource.put (xpandFile, defined);
        
        // make sure all imported resources are registered as well
        for (String imported: file.getImportedExtensions())
            _extensions.registerExtension (imported);

        // make all imported extensions visible for the scope of this compilation unit
        for (String imported: file.getImportedExtensions()) {
            for (NamedFunction f: _extensions.getContributedFunctions(imported))
                fdc.register (f);
        }
    }
        
    public Collection<NamedFunction> getContributedFunctions (String xpandFile) {
        return _functionsByResource.get (OldXtendHelper.normalizeXpandResourceName (xpandFile));
    }
}






