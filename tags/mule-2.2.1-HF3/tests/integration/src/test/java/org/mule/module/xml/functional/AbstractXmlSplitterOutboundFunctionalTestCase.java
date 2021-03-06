/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.xml.functional;

import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public abstract class AbstractXmlSplitterOutboundFunctionalTestCase extends AbstractXmlFunctionalTestCase
{

    public static final String SERVICE_SPLITTER = "service splitter";
    public static final String ROUND_ROBIN_DET = "round robin deterministic";
    public static final String ROUND_ROBIN_INDET = "round robin indeterministic";
    public static final String SPLITTER_ENDPOINT_PREFIX = "service";
    public static final String ROUND_ROBIN_ENDPOINT_PREFIX = "robin";
    public static final String NAME = "name";

    protected String getConfigResources()
    {
        return "org/mule/module/xml/xml-outbound-functional-test.xml";
    }

    protected void doSend(String endpoint) throws IOException, MuleException
    {
        String xml = getConfigAsString();
        MuleClient client = new MuleClient();
        client.dispatch(endpoint, xml, null);
    }

    protected void assertService(String prefix, int index, String service) throws MuleException, IOException
    {
        MuleClient client = new MuleClient();
        MuleMessage response = client.request(prefix + index, TIMEOUT);
        assertNotNull(response);
        assertNotNull(response.getPayload());
        assertTrue(response.getPayload().getClass().getName(), response.getPayload() instanceof Document);
        Document document = (Document) response.getPayload();
        assertEquals("service", document.getRootElement().getName());
        Element element = document.getRootElement();
        assertEquals(service, element.attributeValue(NAME));
    }

    protected void assertServices(String prefix, int index, String[] services) throws MuleException, IOException
    {
        List remaining = new LinkedList(Arrays.asList(services)); // asList is immutable
        while (remaining.size() > 0)
        {
            MuleClient client = new MuleClient();
            MuleMessage response = client.request(prefix + index, TIMEOUT);
            assertNotNull(response);
            assertNotNull(response.getPayload());
            assertTrue(response.getPayload().getClass().getName(), response.getPayload() instanceof Document);
            Document document = (Document) response.getPayload();
            assertEquals("service", document.getRootElement().getName());
            Element element = document.getRootElement();
            String name = element.attributeValue(NAME);
            assertTrue(name, remaining.contains(name));
            int size = remaining.size();
            remaining.remove(name);
            // check we don't delete all instances of same value
            // (apparently not - which makes sense, this is a list, not a set).
            assertEquals(size, remaining.size() + 1);
        }
    }

}