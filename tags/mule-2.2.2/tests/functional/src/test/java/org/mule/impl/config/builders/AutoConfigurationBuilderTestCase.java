/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl.config.builders;

import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.config.ConfigurationException;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.builders.AutoConfigurationBuilder;
import org.mule.config.builders.SimpleConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.testmodels.mule.TestConnector;
import org.mule.tck.testmodels.mule.TestExceptionStrategy;

public class AutoConfigurationBuilderTestCase extends AbstractMuleTestCase
{

    protected MuleContext createMuleContext() throws Exception
    {
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        return muleContextFactory.createMuleContext(new SimpleConfigurationBuilder(null));
    }

    public void testConfigureSpring() throws ConfigurationException, InitialisationException
    {
        ConfigurationBuilder configurationBuilder = new AutoConfigurationBuilder(
            "org/mule/test/spring/config1/test-xml-mule2-config.xml");
        configurationBuilder.configure(muleContext);

        // Just a few of the asserts from AbstractConfigBuilderTestCase
        TestConnector c = (TestConnector) muleContext.getRegistry().lookupConnector("dummyConnector");
        assertNotNull(c);
        assertNotNull(c.getExceptionListener());
        assertTrue(c.getExceptionListener() instanceof TestExceptionStrategy);
    }

    // public void testConfigureGroovy()
    // {
    // // TODO
    // }
    //
    // public void testConfigureGalaxySpring()
    // {
    // // TODO
    // }
    //
    // public void testConfigureUnkownExtension() throws ConfigurationException
    // {
    // ConfigurationBuilder configurationBuilder = new AutoConfigurationBuilder("my.dtd");
    //
    // try
    // {
    // configurationBuilder.configure(muleContext);
    // }
    // catch (ConfigurationException ce)
    // {
    // assertEquals(
    // "No suitable configuration builder for resource \"my.dtd\" found. Check you have configuration module
    // ion your classpath and are using correct file extension.",
    // ce.getCause().getMessage());
    // }
    // catch (Exception e)
    // {
    //            fail("Exception unexpected:" + e);
    //        }
    //    }
}
