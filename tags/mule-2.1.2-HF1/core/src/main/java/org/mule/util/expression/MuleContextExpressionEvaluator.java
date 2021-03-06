/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.util.expression;

import org.mule.MuleServer;
import org.mule.RequestContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleRuntimeException;
import org.mule.config.i18n.CoreMessages;

/**
 * This property extractor exposes mule context information as expressions. This can be context information about
 * the server itself such as the server id or about the current request such as the current service name.
 * <ul>
 * <li>serviceName - returns the name of the service currently processing the event.</li>
 * <li>modelName - returns the name of the model that hosts the current service</li>
 * <li>inboundEndpoint - returns the URI string of the endpoint that received the current messgae.</li>
 * <li>serverId - the Mule instance server Id.</li>
 * <li>clusterId - the Mule instance cluster Id.</li>
 * <li>domainId - the Mule instance domain Id.</li>
 * <li>workingDir - Mule's working directory.</li>
 * <li>homeDir - Mule's home directory</li>
 * </ul>
 */
public class MuleContextExpressionEvaluator implements ExpressionEvaluator
{
    public static final String NAME = "mule";

    /**
     * Extracts a single property from the message
     *
     * @param expression the property expression or expression
     * @param message    the message to extract from
     * @return the result of the extraction or null if the property was not found
     */
    public Object evaluate(String expression, Object message)
    {
        if (expression.equals("serviceName"))
        {
            return getEventContext().getService().getName();
        }
        else if (expression.equals("modelName"))
        {
            return getEventContext().getService().getModel().getName();
        }
        else if (expression.equals("inboundEndpoint"))
        {
            return getEventContext().getEndpointURI();
        }
        else if (expression.equals("serverId"))
        {
            return getMuleContext().getConfiguration().getId();
        }
        else if (expression.equals("clusterId"))
        {
            return getMuleContext().getConfiguration().getClusterId();
        }
        else if (expression.equals("domainId"))
        {
            return getMuleContext().getConfiguration().getDomainId();
        }
        else if (expression.equals("workingDir"))
        {
            return getMuleContext().getConfiguration().getWorkingDirectory();
        }
        else if (expression.equals("homeDir"))
        {
            return getMuleContext().getConfiguration().getMuleHomeDirectory();
        }
        else
        {
            throw new IllegalArgumentException(expression);
        }
    }

    protected MuleContext getMuleContext()
    {
        if(MuleServer.getMuleContext()==null)
        {
             throw new MuleRuntimeException(CoreMessages.objectIsNull("MuleContext"));
        }
        else
        {
            return MuleServer.getMuleContext();
        }
    }

    protected MuleEventContext getEventContext()
    {
        if(RequestContext.getEventContext()==null)
        {
             throw new MuleRuntimeException(CoreMessages.objectIsNull("MuleEventContext"));
        }
        else
        {
            return RequestContext.getEventContext();
        }
    }

    /**
     * Gts the name of the object
     *
     * @return the name of the object
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * Sets the name of the object
     *
     * @param name the name of the object
     */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("setName");
    }
}