/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.management.mbeans;

import org.mule.config.i18n.CoreMessages;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOMessageReceiver;
import org.mule.util.ObjectNameHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The EndpointServiceMBean allows you to check the confiugration of an endpoint and
 * conect/disconnect endpoints manually.
 */
public class EndpointService implements EndpointServiceMBean
{
    /**
     * logger used by this class
     */
    protected transient Log logger = LogFactory.getLog(getClass());

    private UMOImmutableEndpoint endpoint;
    private UMOMessageReceiver receiver;
    private String name;
    private String componentName;

    public EndpointService(UMOImmutableEndpoint endpoint)
    {
        this.endpoint = endpoint;
        init();
    }

    public EndpointService(UMOMessageReceiver receiver)
    {
        if (receiver == null)
        {
            throw new IllegalArgumentException(CoreMessages.objectIsNull("Receiver").getMessage());
        }
        this.endpoint = receiver.getEndpoint();
        this.receiver = receiver;
        this.componentName = receiver.getComponent().getName();
        init();
    }

    private void init()
    {
        if (endpoint == null)
        {
            throw new IllegalArgumentException(CoreMessages.objectIsNull("Endpoint").getMessage());
        }
        if (receiver == null && UMOEndpoint.ENDPOINT_TYPE_RECEIVER.equals(endpoint.getType()))
        {
            throw new IllegalArgumentException(
                "Recevier is null for Endpoint MBean but the endpoint itself is a receiving endpoint");
        }

        name = ObjectNameHelper.getEndpointName(endpoint);
    }

    public String getAddress()
    {
        return endpoint.getEndpointURI().getAddress();
    }

    public String getName()
    {
        return name;
    }

    public boolean isConnected()
    {
        return receiver == null || receiver.isConnected();
    }

    public void connect() throws Exception
    {
        if (receiver != null && !receiver.isConnected())
        {
            receiver.connect();
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Endpoint is already connected");
        }
    }

    public void disconnect() throws Exception
    {
        if (receiver != null && receiver.isConnected())
        {
            receiver.disconnect();
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Endpoint is already disconnected");
        }
    }

    public boolean isSynchronous()
    {
        return endpoint.isSynchronous();
    }

    public String getType()
    {
        return endpoint.getType();
    }

    public String getComponentName()
    {
        return componentName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

}
