/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.LifecycleTransitionResult;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;

import java.util.List;

public class DummyMuleContextAware implements MuleContextAware, Transformer
{

    public void setMuleContext(MuleContext context)
    {
        // empty
    }

    public boolean isSourceTypeSupported(Class aClass)
    {
        return false;
    }

    public List getSourceTypes()
    {
        return null;
    }

    public boolean isAcceptNull()
    {
        return false;
    }

    public boolean isIgnoreBadInput()
    {
        return false;
    }

    public Object transform(Object src) throws TransformerException
    {
        return null;
    }

    public void setReturnClass(Class theClass)
    {
        // empty
    }

    public Class getReturnClass()
    {
        return null;
    }

    public ImmutableEndpoint getEndpoint()
    {
        return null;
    }

    public void setEndpoint(ImmutableEndpoint endpoint)
    {
        // empty
    }

    public LifecycleTransitionResult initialise() throws InitialisationException
    {
        return LifecycleTransitionResult.OK;
    }

    public void setName(String name)
    {
        // empty
    }

    public String getName()
    {
        return null;
    }

}
