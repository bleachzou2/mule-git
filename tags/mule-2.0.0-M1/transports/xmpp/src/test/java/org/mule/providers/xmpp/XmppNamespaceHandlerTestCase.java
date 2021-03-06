/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.xmpp;

import org.mule.tck.FunctionalTestCase;

import junit.framework.Assert;

public class XmppNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "xmpp-namespace-config.xml";
    }

    public void testConfig() throws Exception
    {
        XmppConnector connector = 
            (XmppConnector)managementContext.getRegistry().lookupConnector("xmppConnector");
        
        Assert.assertNotNull(connector);
    }
}
