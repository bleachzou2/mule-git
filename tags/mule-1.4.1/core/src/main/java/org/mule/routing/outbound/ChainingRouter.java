/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.routing.outbound;

import org.mule.config.i18n.CoreMessages;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.routing.CouldNotRouteOutboundMessageException;
import org.mule.umo.routing.RoutePathNotFoundException;
import org.mule.umo.routing.RoutingException;

/**
 * <code>ChainingRouter</code> is used to pass a Mule event through multiple
 * endpoints using the result of the first as the input for the second.
 */

public class ChainingRouter extends FilteringOutboundRouter
{

    public UMOMessage route(UMOMessage message, UMOSession session, boolean synchronous)
        throws RoutingException
    {
        UMOMessage resultToReturn = null;
        if (endpoints == null || endpoints.size() == 0)
        {
            throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), message, null);
        }

        final int endpointsCount = endpoints.size();
        if (logger.isDebugEnabled())
        {
            logger.debug("About to chain " + endpointsCount + " endpoints.");
        }

        // need that ref for an error message
        UMOEndpoint endpoint = null;
        try
        {
            UMOMessage intermediaryResult = message;

            for (int i = 0; i < endpointsCount; i++)
            {
                endpoint = getEndpoint(i, intermediaryResult);
                // if it's not the last endpoint in the chain,
                // enforce the synchronous call, otherwise we lose response
                boolean lastEndpointInChain = (i == endpointsCount - 1);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Sending Chained message '" + i + "': "
                                 + (intermediaryResult == null ? "null" : intermediaryResult.toString()));
                }

                if (!lastEndpointInChain)
                {
                    UMOMessage localResult = send(session, intermediaryResult, endpoint);
                    // Need to propagate correlation info and replyTo, because there
                    // is no guarantee that an external system will preserve headers
                    // (in fact most will not)
                    if (localResult != null && intermediaryResult != null)
                    {
                        processIntermediaryResult(localResult, intermediaryResult);
                    }
                    intermediaryResult = localResult;

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Received Chain result '" + i + "': "
                                     + (intermediaryResult != null ? intermediaryResult.toString() : "null"));
                    }

                    if (intermediaryResult == null)
                    {
                        logger.warn("Chaining router cannot process any further endpoints. "
                                    + "There was no result returned from endpoint invocation: " + endpoint);
                        break;
                    }
                }
                else
                {
                    // ok, the last call,
                    // use the 'sync/async' method parameter
                    if (synchronous)
                    {
                        resultToReturn = send(session, intermediaryResult, endpoint);
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Received final Chain result '" + i + "': "
                                         + (resultToReturn == null ? "null" : resultToReturn.toString()));
                        }
                    }
                    else
                    {
                        // reset the previous call result to avoid confusion
                        resultToReturn = null;
                        dispatch(session, intermediaryResult, endpoint);
                    }
                }
            }

        }
        catch (UMOException e)
        {
            throw new CouldNotRouteOutboundMessageException(message, endpoint, e);
        }
        return resultToReturn;
    }

    public void addEndpoint(UMOEndpoint endpoint)
    {
        if (!endpoint.isRemoteSync())
        {
            logger.debug("Endpoint: "
                         + endpoint.getEndpointURI()
                         + " registered on chaining router needs to be RemoteSync enabled. Setting this property now");
            endpoint.setRemoteSync(true);
        }
        super.addEndpoint(endpoint);
    }

    /**
     * Process intermediary result of invocation. The method will be invoked
     * <strong>only</strong> if both local and intermediary results are available
     * (not null).
     * <p/>
     * Overriding methods must call <code>super(localResult, intermediaryResult)</code>,
     * unless they are modifying the correlation workflow (if you know what that means,
     * you know what you are doing and when to do it).
     * <p/>
     * Default implementation propagates
     * the following properties:
     * <ul>
     * <li>correlationId
     * <li>correlationSequence
     * <li>correlationGroupSize
     * <li>replyTo
     * </ul>
     * @param localResult result of the last endpoint invocation
     * @param intermediaryResult the message travelling across the endpoints
     */
    protected void processIntermediaryResult(UMOMessage localResult, UMOMessage intermediaryResult)
    {
        localResult.setCorrelationId(intermediaryResult.getCorrelationId());
        localResult.setCorrelationSequence(intermediaryResult.getCorrelationSequence());
        localResult.setCorrelationGroupSize(intermediaryResult.getCorrelationGroupSize());
        localResult.setReplyTo(intermediaryResult.getReplyTo());
    }
}
