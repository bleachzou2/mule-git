/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.management;

import org.mule.module.management.agent.RmiRegistryAgent;
import org.mule.module.management.support.AutoDiscoveryJmxSupportFactory;
import org.mule.module.management.support.JmxRegistrationContext;
import org.mule.module.management.support.JmxSupport;
import org.mule.module.management.support.JmxSupportFactory;
import org.mule.tck.AbstractMuleTestCase;

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;

/**
 * This base test case will create a new <code>MBean Server</code> if necessary,
 * and will clean up any registered MBeans in its <code>tearDown()</code> method.
 */
public abstract class AbstractMuleJmxTestCase extends AbstractMuleTestCase
{
    protected MBeanServer mBeanServer;
    protected JmxSupportFactory jmxSupportFactory = AutoDiscoveryJmxSupportFactory.getInstance();
    protected JmxSupport jmxSupport = jmxSupportFactory.getJmxSupport();

    protected void doSetUp() throws Exception
    {
        RmiRegistryAgent rmiRegistryAgent = new RmiRegistryAgent();
        rmiRegistryAgent.setMuleContext(muleContext);
        rmiRegistryAgent.initialise();
        muleContext.getRegistry().registerAgent(rmiRegistryAgent);

        mBeanServer = ManagementFactory.getPlatformMBeanServer();

    }

    protected void unregisterMBeansByMask(final String mask) throws Exception
    {
        Set objectInstances = mBeanServer.queryMBeans(jmxSupport.getObjectName(mask), null);
        for (Iterator it = objectInstances.iterator(); it.hasNext();)
        {
            ObjectInstance instance = (ObjectInstance) it.next();
            try
            {
                mBeanServer.unregisterMBean(instance.getObjectName());
            }
            catch (Exception e)
            {
                // ignore
            }
        }
    }

    protected void doTearDown() throws Exception
    {
        String domainName = jmxSupport.getDomainName(muleContext);
        unregisterMBeansByMask(domainName + ":*");
        unregisterMBeansByMask(domainName + ".1:*");
        unregisterMBeansByMask(domainName + ".2:*");
        mBeanServer = null;
    }

    public void testDummy()
    {
        // this method only exists to silence the test runner
    }

}
