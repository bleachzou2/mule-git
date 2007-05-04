/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.umo.routing;

import org.mule.umo.MessagingException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;

/**
 * <code>UMOOutboundRouterCollection</code> is responsible for holding all outbound routers for a service component.
 */

public interface UMOOutboundRouterCollection extends UMORouterCollection
{
    /**
     * Prepares one or more events to be dispached by a Message Dispatcher.
     * 
     * @param message The source Message
     * @param session The current session
     * @return a list containing 0 or events to be dispatched
     * @throws RoutingException If any of the events cannot be created.
     */
    UMOMessage route(UMOMessage message, UMOSession session, boolean synchronous) throws MessagingException;

    /**
     * A helper method for finding out which endpoints a message would be routed to
     * without actually routing the the message.
     * 
     * @param message the message to retrieve endpoints for
     * @return an array of UMOEndpoint objects or an empty array
     * @throws RoutingException if there is a filter exception
     */
    UMOEndpoint[] getEndpointsForMessage(UMOMessage message) throws MessagingException;

    /**
     * Determines if any endpoints have been set on this router.
     * 
     * @return
     */
    boolean hasEndpoints();
}
