/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.oracle.jms;

import org.mule.impl.endpoint.EndpointURIEndpointBuilder;
import org.mule.providers.AbstractConnector;
import org.mule.tck.providers.AbstractMessageReceiverTestCase;
import org.mule.umo.UMOComponent;
import org.mule.umo.endpoint.UMOEndpointBuilder;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.provider.UMOConnector;
import org.mule.umo.provider.UMOMessageReceiver;

import com.mockobjects.dynamic.Mock;

public class OracleJmsMessageReceiverTestCase extends AbstractMessageReceiverTestCase
{

    private OracleJmsConnector connector = null;

    public UMOConnector getConnector() throws Exception
    {
        if (connector == null)
        {
            connector = new OracleJmsConnector();
            connector.setName("TestConnector");
            connector.setUrl("jdbc:oracle:oci:@TEST_DB");
            connector.setUsername("scott");
            connector.setPassword("tiger");
            connector.initialise();
        }
        return connector;
    }

    public UMOMessageReceiver getMessageReceiver() throws Exception
    {
        getConnector();
        endpoint = getEndpoint();
        Mock mockComponent = new Mock(UMOComponent.class);
        return new OracleJmsMessageReceiver((AbstractConnector)endpoint.getConnector(),
            (UMOComponent)mockComponent.proxy(), endpoint);
    }

    public UMOImmutableEndpoint getEndpoint() throws Exception
    {
        UMOEndpointBuilder builder = new EndpointURIEndpointBuilder("jms://TEST_QUEUE", managementContext);
        builder.setConnector(getConnector());
        endpoint = managementContext.getRegistry().lookupEndpointFactory().createInboundEndpoint(builder,
            managementContext);
        return endpoint;
    }
}
