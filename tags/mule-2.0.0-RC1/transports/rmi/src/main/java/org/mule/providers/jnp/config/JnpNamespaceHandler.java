/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.jnp.config;

import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.providers.jnp.JnpConnector;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Reigsters a Bean Definition Parser for handling <code><tcp:connector></code> elements.
 *
 */
public class JnpNamespaceHandler extends NamespaceHandlerSupport
{
    public void init()
    {
        registerBeanDefinitionParser("connector", new MuleOrphanDefinitionParser(JnpConnector.class, true));
    }
}