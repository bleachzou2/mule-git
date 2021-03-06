/*
 s * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */

package org.mule.interceptors;

import java.util.Iterator;
import java.util.List;

import org.mule.umo.Invocation;
import org.mule.umo.UMOException;
import org.mule.umo.UMOInterceptor;
import org.mule.umo.UMOInterceptorStack;
import org.mule.umo.UMOMessage;
import org.mule.umo.lifecycle.Disposable;
import org.mule.umo.lifecycle.Initialisable;
import org.mule.umo.lifecycle.InitialisationException;

/**
 * TODO: document this class
 * 
 * @author <a href="mailto:gnt@codehaus.org">Guillaume Nodet</a>
 * @version $Revision$
 */
public class InterceptorStack implements UMOInterceptorStack, Initialisable, Disposable
{

    private List interceptors;

    public InterceptorStack()
    {
    }

    public InterceptorStack(List interceptors)
    {
        this.interceptors = interceptors;
    }

    public UMOMessage intercept(Invocation invocation) throws UMOException
    {
        return new Invoc(invocation).execute();
    }

    private class Invoc extends Invocation
    {
        private int cursor = 0;
        private Invocation invocation;

        public Invoc(Invocation invocation)
        {
            super(invocation.getDescriptor(), invocation.getMessage(), invocation);
            this.invocation = invocation;
        }

        public UMOMessage execute() throws UMOException
        {
            if (interceptors != null && cursor < interceptors.size()) {
                UMOInterceptor interceptor = (UMOInterceptor) interceptors.get(cursor);
                cursor++;
                setMessage(interceptor.intercept(this));
            } else {
                invocation.setMessage(getMessage());
                setMessage(invocation.execute());
            }
            return getMessage();
        }

    }

    public List getInterceptors()
    {
        return interceptors;
    }

    public void setInterceptors(List interceptors)
    {
        this.interceptors = interceptors;
    }

    public void initialise() throws InitialisationException
    {
        for (Iterator it = interceptors.iterator(); it.hasNext();) {
            UMOInterceptor interceptor = (UMOInterceptor) it.next();
            if (interceptor instanceof Initialisable) {
                ((Initialisable) interceptor).initialise();
            }
        }
    }

    public void dispose()
    {
        for (Iterator it = interceptors.iterator(); it.hasNext();) {
            UMOInterceptor interceptor = (UMOInterceptor) it.next();
            if (interceptor instanceof Disposable) {
                ((Disposable) interceptor).dispose();
            }
        }
    }

}
