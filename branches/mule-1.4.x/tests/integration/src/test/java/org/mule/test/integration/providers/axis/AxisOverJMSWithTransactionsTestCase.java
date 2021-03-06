/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.providers.axis;

import org.mule.MuleManager;
import org.mule.extras.client.MuleClient;
import org.mule.impl.MuleMessage;
import org.mule.providers.soap.axis.AxisConnector;
import org.mule.tck.FunctionalTestCase;
import org.mule.umo.UMOMessage;

public class AxisOverJMSWithTransactionsTestCase extends FunctionalTestCase
{
    public void testTransactionPropertiesOnEndpoint() throws Exception {
        Object[] connectorArray = MuleManager.getInstance().getConnectors().values().toArray();
        AxisConnector connector = null;
        for (int i = 0; i < connectorArray.length; i++)
        {
            if (connectorArray[i] instanceof AxisConnector)
            {
                connector = (AxisConnector)connectorArray[i];
            }
        }
        assertNotNull(connector);
        //This no longer works because the Axis descriptor name is made unique per connector
        //MuleDescriptor axisDescriptor = (MuleDescriptor)MuleManager.getInstance().lookupModel(ModelHelper.SYSTEM_MODEL).getDescriptor(connector.AXIS_SERVICE_COMPONENT_NAME);
        //assertNotNull(axisDescriptor.getInboundRouter().getEndpoint("jms.TestComponent").getTransactionConfig());
    }

    public void testTransactionsOverAxis() throws Exception{
        MuleClient client = new MuleClient();
        client.dispatch("axis:jms://TestComponent?method=echo", new MuleMessage("test"));
        UMOMessage message = client.receive("jms://testout", 5000);
        assertNotNull(message.getPayload());
        assertTrue(message.getPayloadAsString().equals("test"));
    }

    protected String getConfigResources()
    {
        return "org/mule/test/integration/providers/axis/axis-over-jms-config.xml";
    }

}


