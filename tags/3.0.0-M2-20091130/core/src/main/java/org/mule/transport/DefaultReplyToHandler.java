/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport;

import org.mule.DefaultMuleEvent;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.EndpointFactory;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.transport.DispatchException;
import org.mule.api.transport.ReplyToHandler;
import org.mule.config.i18n.CoreMessages;
import org.mule.service.AbstractService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>DefaultReplyToHandler</code> is responsible for processing a message
 * replyTo header.
 */

public class DefaultReplyToHandler implements ReplyToHandler
{
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(getClass());

    private volatile List transformers;
    private final Map<String, ImmutableEndpoint> endpointCache = new HashMap<String, ImmutableEndpoint>();
    protected MuleContext muleContext;

    public DefaultReplyToHandler(List transformers, MuleContext muleContext)
    {
        this.transformers = transformers;
        this.muleContext = muleContext;
    }

    public void processReplyTo(MuleEvent event, MuleMessage returnMessage, Object replyTo) throws MuleException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("sending reply to: " + replyTo);
        }

        String replyToEndpoint = replyTo.toString();

        // get the endpoint for this url
        OutboundEndpoint endpoint = getEndpoint(event, replyToEndpoint);

        // make sure remove the replyTo property as not cause a a forever
        // replyto loop
        returnMessage.removeProperty(MuleProperties.MULE_REPLY_TO_PROPERTY);
        
        // MULE-4617. This is fixed with MULE-4620, but lets remove this property
        // anyway as it should never be true from a replyTo dispatch
        returnMessage.removeProperty(MuleProperties.MULE_REMOTE_SYNC_PROPERTY);
        
        // Create the replyTo event asynchronous
        MuleEvent replyToEvent = new DefaultMuleEvent(returnMessage, endpoint, event.getSession(), false);

        // dispatch the event
        try
        {
            ((AbstractService) event.getService()).getStatistics().incSentReplyToEvent();
            endpoint.dispatch(replyToEvent);
            if (logger.isInfoEnabled())
            {
                logger.info("reply to sent: " + endpoint);
            }
        }
        catch (Exception e)
        {
            throw new DispatchException(
                CoreMessages.failedToDispatchToReplyto(endpoint),
                replyToEvent.getMessage(), replyToEvent.getEndpoint(), e);
        }

    }

    protected synchronized OutboundEndpoint getEndpoint(MuleEvent event, String endpointUri) throws MuleException
    {
        OutboundEndpoint endpoint = (OutboundEndpoint) endpointCache.get(endpointUri);
        if (endpoint == null)
        {
            EndpointFactory endpointFactory = muleContext.getRegistry().lookupEndpointFactory();
            EndpointBuilder endpointBuilder = endpointFactory.getEndpointBuilder(endpointUri);
            if (transformers == null)
            {
                endpointBuilder.setTransformers(event.getEndpoint().getResponseTransformers());
            }
            endpoint = endpointFactory.getOutboundEndpoint(endpointBuilder);
            endpointCache.put(endpointUri, endpoint);
        }
        return endpoint;
    }

    public List getTransformers()
    {
        return transformers;
    }

    public void setTransformers(List transformers)
    {
        this.transformers = transformers;
    }

}
