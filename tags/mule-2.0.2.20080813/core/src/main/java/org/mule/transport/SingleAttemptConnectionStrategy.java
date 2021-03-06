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

import org.mule.api.transport.Connectable;
import org.mule.config.i18n.CoreMessages;

/**
 * Attempts to make a connection once and fails if there is an exception
 */
public class SingleAttemptConnectionStrategy extends AbstractConnectionStrategy
{
    public void doConnect(Connectable connectable) throws FatalConnectException
    {
        try
        {
            connectable.connect();
        }
        catch (Exception e)
        {
            throw new FatalConnectException(
                CoreMessages.reconnectStrategyFailed(this.getClass(), this.getDescription(connectable)), 
                e, connectable);

        }
    }

    /**
     * Resets any state stored in the retry strategy
     */
    public void resetState()
    {
        // no op
    }
}
