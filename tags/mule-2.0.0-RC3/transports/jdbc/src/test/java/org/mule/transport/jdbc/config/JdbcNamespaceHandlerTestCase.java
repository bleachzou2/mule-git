/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.jdbc.config;

import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.jdbc.JdbcConnector;
import org.mule.transport.jdbc.test.TestDataSource;


/**
 * Tests the "jdbc" namespace.
 */
public class JdbcNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "jdbc-namespace-config.xml";
    }

    public void testWithDataSource() throws Exception
    {
        JdbcConnector c = (JdbcConnector) muleContext.getRegistry().lookupConnector("jdbcConnector1");
        assertNotNull(c);        

        assertTrue(c.getDataSource() instanceof TestDataSource);
        assertNull(c.getQueries());       
    }

    public void testWithDataSourceViaJndi() throws Exception
    {
        JdbcConnector c = (JdbcConnector) muleContext.getRegistry().lookupConnector("jdbcConnector2");
        assertNotNull(c);
        
        assertTrue(c.getDataSource() instanceof TestDataSource);
        assertNull(c.getQueries());
        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }
    
    public void testFullyConfigured() throws Exception
    {
        JdbcConnector c = (JdbcConnector) muleContext.getRegistry().lookupConnector("jdbcConnector3");
        assertNotNull(c);
        
        assertTrue(c.getDataSource() instanceof TestDataSource);
        
//        assertEquals(2,c.getPropertyExtractors().size());
//        assertTrue(c.getPropertyExtractors().iterator().next() instanceof PropertyExtractor);
        assertNotNull(c.getQueries());
        assertEquals(3, c.getQueries().size());
        
        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
    }
    
    
    public void testEndpointQueryOverride() throws Exception
    {
        JdbcConnector c = (JdbcConnector) muleContext.getRegistry().lookupConnector("jdbcConnector3");
        ImmutableEndpoint testJdbcEndpoint = muleContext.getRegistry()
            .lookupEndpointFactory()
            .getInboundEndpoint("testJdbcEndpoint");
        
        //On connector, not overridden
        assertNotNull(c.getQuery(testJdbcEndpoint, "getTest"));
        
        //On connector, overridden on endpoint
        assertNotNull(c.getQuery(testJdbcEndpoint, "getTest2"));
        assertEquals("OVERRIDDEN VALUE", c.getQuery(testJdbcEndpoint, "getTest2"));
        
        //Only on endpoint
        assertNotNull(c.getQuery(testJdbcEndpoint, "getTest3"));

        //Does not exist on either
        assertNull(c.getQuery(testJdbcEndpoint, "getTest4"));
    }
}
