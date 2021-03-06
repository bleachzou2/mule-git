/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.test.providers.email;

import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.providers.email.Pop3Connector;
import org.mule.providers.email.SmtpConnector;
import org.mule.providers.service.ConnectorFactory;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.umo.provider.UMOConnector;

/**
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public class ConnectorFactoryTestCase extends AbstractMuleTestCase
{
    public void testCreatePop3Connector() throws Exception
    {
        MuleEndpointURI url = new MuleEndpointURI("pop3://ross:password@pop3.muleumo.org");
        UMOConnector cnn = ConnectorFactory.createConnector(url);
        assertTrue(cnn instanceof Pop3Connector);
        Pop3Connector pop3 = (Pop3Connector) cnn;

        url = new MuleEndpointURI("pop3://ross:password@pop3.muleumo.org:123?checkFrequency=3001");
        cnn = ConnectorFactory.createConnector(url);
        assertTrue(cnn instanceof Pop3Connector);
        pop3 = (Pop3Connector) cnn;
        assertEquals(3001, pop3.getCheckFrequency());
    }

    public void testCreateSmtpConnector() throws Exception
    {
        MuleEndpointURI url = new MuleEndpointURI("smtp://ross:password@smtp.muleumo.org");
        UMOConnector cnn = ConnectorFactory.createConnector(url);
        assertTrue(cnn instanceof SmtpConnector);
        SmtpConnector smtp = (SmtpConnector) cnn;
        assertEquals("smtp.muleumo.org", smtp.getHostname());
        assertEquals(25, smtp.getPort());
        assertEquals("ross", smtp.getUsername());
        assertEquals("password", smtp.getPassword());

        url = new MuleEndpointURI("smtp://ross:password@smtp.muleumo.org:1023?fromAddress=admin@muleumo.org&subject=Hello");
        cnn = ConnectorFactory.createConnector(url);
        assertTrue(cnn instanceof SmtpConnector);
        smtp = (SmtpConnector) cnn;
        assertEquals("smtp.muleumo.org", smtp.getHostname());
        assertEquals(1023, smtp.getPort());
        assertEquals("ross", smtp.getUsername());
        assertEquals("password", smtp.getPassword());
        assertEquals("admin@muleumo.org", smtp.getFromAddress());
        assertEquals("Hello", smtp.getSubject());
    }
}
