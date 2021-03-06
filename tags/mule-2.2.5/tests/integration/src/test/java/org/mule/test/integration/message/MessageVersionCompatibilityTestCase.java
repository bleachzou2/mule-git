/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.message;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

/**
 * Test case for EE-1820
 */
public class MessageVersionCompatibilityTestCase extends FunctionalTestCase
{
    private int TIMEOUT = 5000;
    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/messaging/message-version-compatibility.xml";
    }

    public void testOldToOld() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("vm://in1", "test", null);

        MuleMessage reply = client.request("vm://out1", TIMEOUT);
        assertNotNull(reply);
        assertEquals("test", reply.getPayload());
    }

    public void testOldToNew() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("vm://in2", "test", null);

        MuleMessage reply = client.request("vm://out2", TIMEOUT);
        assertNotNull(reply);
        assertEquals("test", reply.getPayload());
    }

    public void testNewToOld() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("vm://in3", "test", null);

        MuleMessage reply = client.request("vm://out3", TIMEOUT);
        // No output is received because the receiver throws an exception:
        // "java.lang.IllegalArgumentException: Session variable ... is malfomed and cannot be read"
        assertNull(reply);
    }
    
    public void testNewToNew() throws Exception
    {
        MuleClient client = new MuleClient();
        client.dispatch("vm://in4", "test", null);

        MuleMessage reply = client.request("vm://out4", TIMEOUT);
        assertNotNull(reply);
        assertEquals("test", reply.getPayload());
    }
}
