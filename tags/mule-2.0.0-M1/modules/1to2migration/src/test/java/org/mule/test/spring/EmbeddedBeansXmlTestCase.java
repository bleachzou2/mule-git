/*
 * $Id:EmbeddedBeansXmlTestCase.java 5187 2007-02-16 18:00:42Z rossmason $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.spring;

import org.mule.config.ConfigurationBuilder;
import org.mule.config.ExceptionHelper;
import org.mule.config.builders.MuleXmlConfigurationBuilder;
import org.mule.config.spring.LegacyXmlException;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.umo.UMOManagementContext;

public class EmbeddedBeansXmlTestCase extends AbstractMuleTestCase
{

    protected String getConfigResources()
    {
        return "test-embedded-spring-config.xml";
    }


    //@java.lang.Override
    protected UMOManagementContext createManagementContext() throws Exception
    {
        return null;
    }

    public void testEmbeddedXmlNotSupported() throws Exception
    {
         ConfigurationBuilder builder = new MuleXmlConfigurationBuilder();
        try
        {
            builder.configure(getConfigResources(), null);
        }
        catch (Exception e)
        {
            Throwable ex = ExceptionHelper.getRootException(e);
            assertTrue("Root exception should be of type LegacyXmlException not: " + ex.getClass(), ex instanceof LegacyXmlException);
            LegacyXmlException lxe = (LegacyXmlException)ex;
            assertEquals(1, lxe.getErrors().size());
        }
    }
}
