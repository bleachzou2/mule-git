/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.routing.outbound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.routing.CouldNotRouteOutboundMessageException;
import org.mule.umo.routing.RoutingException;

/**
 * <code>ChainingRouter</code> is used to pass a Mule event through multiple endpoints
 * using the result of the first and the input for the second
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */

public class ChainingRouter extends FilteringOutboundRouter
{
    /**
     * logger used by this class
     */
    protected static transient Log logger = LogFactory.getLog(ChainingRouter.class);

    public UMOMessage route(UMOMessage message, UMOSession session, boolean synchronous) throws RoutingException
    {
        UMOMessage result = null;
        if (endpoints == null || endpoints.size() == 0)
        {
            throw new RoutingException("No endpoints are set on this router, cannot route message");
        }
        try
        {

            if (synchronous)
            {
                result = message;
                for(int i=0;i<endpoints.size();i++) {
                    result = send(session, result, (UMOEndpoint) endpoints.get(i));
                }

            } else
            {
                logger.info("Invocation is asynchronous no result will be returned for any further inocations");
                dispatch(session, message, (UMOEndpoint) endpoints.get(0));
            }
        } catch (UMOException e)
        {
            throw new CouldNotRouteOutboundMessageException(e.getMessage(), e, message);
        }
        return result;
    }
}
