/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.file;

import org.mule.impl.endpoint.EndpointURIEndpointBuilder;
import org.mule.providers.AbstractConnector;
import org.mule.tck.FunctionalTestCase;
import org.mule.transformers.TransformerUtils;
import org.mule.transformers.simple.ByteArrayToSerializable;
import org.mule.transformers.simple.SerializableToByteArray;
import org.mule.umo.UMOException;
import org.mule.umo.endpoint.UMOEndpointBuilder;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

public class ConnectorServiceOverridesTestCase extends FunctionalTestCase
{

    protected String getConfigResources()
    {
        return "test-connector-config.xml";
    }

    public void testServiceOverrides() throws InterruptedException
    {
        FileConnector c = (FileConnector) managementContext.getRegistry().lookupConnector("fileConnector2");
        assertNotNull(c);
        assertNotNull(c.getServiceOverrides());
        assertEquals("org.mule.transformers.simple.ByteArrayToSerializable", c.getServiceOverrides().get(
            "inbound.transformer"));
        assertNotNull(TransformerUtils.firstOrNull(c.getDefaultInboundTransformers()));
        assertNotNull(TransformerUtils.firstOrNull(c.getDefaultOutboundTransformers()));
        assertTrue(TransformerUtils.firstOrNull(c.getDefaultInboundTransformers()) instanceof ByteArrayToSerializable);
        assertTrue(TransformerUtils.firstOrNull(c.getDefaultOutboundTransformers()) instanceof SerializableToByteArray);
    }

    public void testServiceOverrides2() throws InterruptedException
    {
        FileConnector c = (FileConnector) managementContext.getRegistry().lookupConnector("fileConnector1");
        assertNotNull(c);
        assertNull(c.getServiceOverrides());

        c = (FileConnector) managementContext.getRegistry().lookupConnector("fileConnector2");
        assertNotNull(c);
        assertNotNull(c.getServiceOverrides());

        c = (FileConnector) managementContext.getRegistry().lookupConnector("fileConnector3");
        assertNotNull(c);
        assertNull(c.getServiceOverrides());
    }

    public void testServiceOverrides3() throws InterruptedException, UMOException
    {
        // UMOEndpointURI uri = new MuleEndpointURI("file:///temp?connector=fileConnector1");
        UMOImmutableEndpoint endpoint = managementContext.getRegistry().lookupInboundEndpoint(
            "file:///temp?connector=fileConnector1", managementContext);

        assertNotNull(endpoint);
        assertNotNull(endpoint.getConnector());
        assertNull(((AbstractConnector) endpoint.getConnector()).getServiceOverrides());

        FileConnector c = (FileConnector) managementContext.getRegistry().lookupConnector("fileConnector2");
        assertNotNull(c);
        assertNotNull(c.getServiceOverrides());

        UMOEndpointBuilder builder = new EndpointURIEndpointBuilder("file:///temp?connector=fileConnector1",
            managementContext);
        builder.setConnector(c);
        endpoint = managementContext.getRegistry().lookupEndpointFactory().createInboundEndpoint(builder,
            managementContext);
        assertNotNull(((AbstractConnector) endpoint.getConnector()).getServiceOverrides());

        UMOEndpointBuilder builder2 = new EndpointURIEndpointBuilder("file:///temp?connector=fileConnector3",
            managementContext);
        builder.setConnector(c);
        endpoint = managementContext.getRegistry().lookupEndpointFactory().createInboundEndpoint(builder2,
            managementContext);
        assertNull(((AbstractConnector) endpoint.getConnector()).getServiceOverrides());

        UMOEndpointBuilder builder3 = new EndpointURIEndpointBuilder("file:///temp?connector=fileConnector2",
            managementContext);
        builder.setConnector(c);
        endpoint = managementContext.getRegistry().lookupEndpointFactory().createInboundEndpoint(builder3,
            managementContext);
        assertNotNull(((AbstractConnector) endpoint.getConnector()).getServiceOverrides());

    }
}
