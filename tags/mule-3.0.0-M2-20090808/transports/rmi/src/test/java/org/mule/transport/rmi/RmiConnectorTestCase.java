/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.rmi;

import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;
import org.mule.transport.rmi.RmiConnector;

public class RmiConnectorTestCase extends AbstractConnectorTestCase
{

    @Override
    public Connector createConnector() throws Exception
    {
        RmiConnector c = new RmiConnector();
        c.setName("RmiConnector");
        c.setSecurityManager(null);
        return c;
    }

    public String getTestEndpointURI()
    {
        return "rmi://localhost:1099";
    }

    public Object getValidMessage() throws Exception
    {
        return "Hello".getBytes();
    }

    public void testProperties() throws Exception
    {
        RmiConnector c = (RmiConnector)getConnector();

        String securityPolicy = "rmi.policy";
        String serverCodebase = "file:///E:/projects/MyTesting/JAVA/rmi/classes/";

        c.setSecurityPolicy(securityPolicy);
        assertNotNull(c.getSecurityPolicy());
        c.setServerCodebase(serverCodebase);
        assertEquals(serverCodebase, c.getServerCodebase());
    }

}
