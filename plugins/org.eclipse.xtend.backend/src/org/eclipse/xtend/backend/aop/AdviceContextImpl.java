/*
Copyright (c) 2008 Arno Haase.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
    Arno Haase - initial API and implementation
*/
package org.eclipse.xtend.backend.aop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.xtend.backend.common.AdviceContext;
import org.eclipse.xtend.backend.common.Function;
import org.eclipse.xtend.backend.util.DoubleKeyCache;
import org.eclipse.xtend.backend.util.ObjectWrapper;
import org.eclipse.xtend.backend.util.Triplet;


/**
 * This class represents all currently registered advice. It is
 *  designed to efficiently handle dynamically registered advice, 
 *  e.g. for the scope of an invocation.<br>
 *  
 * In order to allow aggressive caching, it is immutable and creates
 *  a copy when additional advice is registered. This design decision
 *  assumes that registering new advice is very infrequent compared to the
 *  invocation of functions.
 * 
 * @author Arno Haase (http://www.haase-consulting.com)
 */
public final class AdviceContextImpl implements AdviceContext {
    private final List<AroundAdvice> _advice = new ArrayList<AroundAdvice> ();
    
    private final DoubleKeyCache <String, Function, AdvisedFunction> _advisedFunctionCache = new DoubleKeyCache<String, Function, AdvisedFunction> () {
        @Override
        protected AdvisedFunction create (String functionName, Function f) {
            final List<AroundAdvice> applicableAdvice = new ArrayList<AroundAdvice> ();
            
            for (AroundAdvice advice: _advice)
                if (advice.getPointcut().matches (functionName, f))
                    applicableAdvice.add (advice);

            return new AdvisedFunction (functionName, f, applicableAdvice);
        }
    };
    
    private final Map<Triplet <Function, AroundAdvice, List<?>>, ObjectWrapper> _resultCache;
    
    
    public AdviceContextImpl () {
        this (new HashMap<Triplet<Function,AroundAdvice,List<?>>, ObjectWrapper> ());
    }

    private AdviceContextImpl (Map<Triplet <Function, AroundAdvice, List<?>>, ObjectWrapper> resultCache) {
        _resultCache = resultCache;
    }
    
    public AdviceContextImpl copyWithAdvice (AroundAdvice advice) {
        // the result cache survives the addition (and going out-of-scope!) of advice. This
        //  is possible because newly applied advice is applied "around" advice applied earlier.
        final AdviceContextImpl result = new AdviceContextImpl (_resultCache);

        result._advice.addAll (_advice);
        result._advice.add (advice);
        
        return result;
    }

    //TODO test this (including the order in which advice is applied)!!!
    
    /**
     * returns the advice to be applied to this function, starting with the innermost
     *  advice, i.e. the advice that is to be directly applied to the result of the
     *  advised function. 
     */
    public AdvisedFunction getAdvice (String functionName, Function f) {
        return _advisedFunctionCache.get (functionName, f);
    }

    public Map<Triplet<Function, AroundAdvice, List<?>>, ObjectWrapper> getResultCache () {
        return _resultCache;
    }
}


