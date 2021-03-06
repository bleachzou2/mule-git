/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.email;

import org.mule.umo.provider.UMOConnector;

/**
 * Simple tests for pulling from an IMAP server.
 */
public class ImapConnectorTestCase extends AbstractReceivingMailConnectorTestCase
{

    public ImapConnectorTestCase()
    {
        super("ImapConnector");
    }
    
    public UMOConnector getConnector(boolean init) throws Exception
    {
        ImapConnector connector = new ImapConnector();
        connector.setName(getConnectorName());
        connector.setCheckFrequency(POLL_PERIOD_MS);
        if (init)
        {
            connector.initialise();
        }
        return connector;
    }

    public String getTestEndpointURI()
    {
        return getImapTestEndpointURI();
    }

}
