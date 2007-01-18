/*
 * $Id: $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.builders;

import org.mule.MuleServer;
import org.mule.config.ConfigurationException;
import org.mule.umo.UMOException;
import org.mule.umo.manager.UMOAgent;
import org.mule.umo.manager.UMOManager;
import org.mule.util.ClassUtils;

/**
 * EXPERIMENTAL!!!
 *
 * This builder configures a dummy node so that Mule will run without 
 * anything configured (except I do have 3 agents configured, but you 
 * wouldn't need these). 
 *
 * I'm extending the QuickConfigurationBuilder a) because I thought I might
 * needs its methods, and b) because I didn't want to heavily rewrite MuleServer
 * However, rewriting the whole MuleServer/ConfigurationBuilder stuff is inevitable
 */
public class MuleIdleConfigurationBuilder extends QuickConfigurationBuilder
{
    private boolean configured = false;

    /**
     * Constructs a default builder
     */
    public MuleIdleConfigurationBuilder()
    {
        super();
    }

    /**
     */
    public MuleIdleConfigurationBuilder(boolean disposeCurrent)
    {
        super(disposeCurrent);
    }

    public boolean isConfigured()
    {
        return configured;
    }

    public UMOManager configure(String configResources, String startupPropertiesFile)
        throws ConfigurationException
    {
        try
        {
            Class scannerAgentClass = ClassUtils.loadClass("org.mule.impl.internal.admin.ConfigScannerAgent", MuleIdleConfigurationBuilder.class);
            UMOAgent scannerAgent = (UMOAgent)scannerAgentClass.newInstance();
            manager.registerAgent(scannerAgent);

            Class jmxAgentClass = ClassUtils.loadClass("org.mule.management.agents.JmxAgent", MuleIdleConfigurationBuilder.class);
            UMOAgent jmxAgent = (UMOAgent)jmxAgentClass.newInstance();
            jmxAgent.setName("jmxAgent");
            manager.registerAgent(jmxAgent);

            Class mx4jAgentClass = ClassUtils.loadClass("org.mule.management.agents.Mx4jAgent", MuleIdleConfigurationBuilder.class);
            UMOAgent mx4jAgent = (UMOAgent)mx4jAgentClass.newInstance();
            mx4jAgent.setName("mx4jAgent");
            manager.registerAgent(mx4jAgent);
        }
        catch (Exception e)
        {
            // TODO: change
            System.err.println(e.toString());
        }

        try
        {
            manager.start();
        }
        catch (UMOException umoe)
        {
            throw new ConfigurationException(umoe);
        }

        configured = true;
        return manager;
    }
}

