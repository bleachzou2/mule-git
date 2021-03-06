/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.jms.weblogic;

import org.mule.providers.jms.JmsTopicResolver;
import org.mule.tck.AbstractMuleTestCase;

public class WeblogicJmsConnectorTestCase extends AbstractMuleTestCase
{
    public void testConfigurationDefaults()
    {
        WeblogicJmsConnector c = new WeblogicJmsConnector();
        // TODO has to be confirmed for Weblogic
        assertTrue(c.isEagerConsumer());
        JmsTopicResolver resolver = c.getTopicResolver();
        assertNotNull("Topic resolver must not be null.", resolver);
        assertTrue("Wrong topic resolver configured on the connector.",
                   resolver instanceof WeblogicJmsTopicResolver);
    }
}
