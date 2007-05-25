/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.tck;

import org.mule.MuleManager;
import org.mule.config.MuleConfiguration;
import org.mule.impl.MuleDescriptor;
import org.mule.tck.testmodels.mule.TestConnector;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOException;
import org.mule.umo.UMOSession;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.manager.UMOManager;
import org.mule.umo.model.UMOModel;
import org.mule.umo.transformer.UMOTransformer;
import org.mule.util.FileUtils;
import org.mule.util.StringMessageUtils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>AbstractMuleTestCase</code> is a base class for Mule testcases. This
 * implementation provides services to test code for creating mock and test objects.
 */
public abstract class AbstractMuleTestCase extends TestCase
{
    protected final Log logger = LogFactory.getLog(getClass());

    // This should be set to a string message describing any prerequisites not met
    private boolean offline = System.getProperty("org.mule.offline", "false").equalsIgnoreCase("true");
    private boolean testLogging = System.getProperty("org.mule.test.logging", "false").equalsIgnoreCase(
        "true");

    private static Map testCounters;

    public AbstractMuleTestCase()
    {
        super();
        if (testCounters == null)
        {
            testCounters = new HashMap();
        }
        addTest();
    }

    protected void addTest()
    {
        TestInfo info = (TestInfo) testCounters.get(getClass().getName());
        if (info == null)
        {
            info = new TestInfo(getClass().getName());
            testCounters.put(getClass().getName(), info);
        }
        info.incTestCount();
    }

    protected void setDisposeManagerPerSuite(boolean val)
    {
        getTestInfo().setDisposeManagerPerSuite(val);
    }

    protected TestInfo getTestInfo()
    {
        TestInfo info = (TestInfo) testCounters.get(getClass().getName());
        if (info == null)
        {
            info = new TestInfo(getClass().getName());
            testCounters.put(getClass().getName(), info);
        }
        return info;
    }

    private void clearAllCounters()
    {
        if (testCounters != null)
        {
            testCounters.clear();
        }
        log("Cleared all counters");
    }

    private void clearCounter()
    {
        if (testCounters != null)
        {
            testCounters.remove(getClass().getName());
        }
        log("Cleared counter: " + getClass().getName());
    }

    private void log(String s)
    {
        if (testLogging)
        {
            System.err.println(s);
        }
    }

    public String getName()
    {
        return super.getName().substring(4).replaceAll("([A-Z])", " $1").toLowerCase() + " ";
    }

    public void run(TestResult result) 
    {
        if (this.isTestCaseDisabled())
        {
            logger.info("**** " + this.getClass().getName() + " disabled");
            return;
        }
        
        super.run(result);
    }
     
    /**
     * Subclasses can override this method to skip the execution of the entire test class.
     * 
     * @return <code>true</code> if the test class should not be run.
     */
    protected boolean isTestCaseDisabled()
    {
        return false;
    }
    
    /**
     * Shamelessly copy from Spring's ConditionalTestCase so in MULE-2.0 we can extend
     * this class from ConditionalTestCase.
     * <p/>
     * Subclasses can override <code>isDisabledInThisEnvironment</code> to skip a single test.
     */
    public void runBare() throws Throwable 
    {
        // getName will return the name of the method being run
        if (this.isDisabledInThisEnvironment(this.getName())) 
        {
            logger.info("**** " + this.getClass().getName() + "." + this.getName() + " disabled in this environment: ");
            return;
        }
        
        // Let JUnit handle execution
        super.runBare();
    }

    /**
     * Should this test run?
     * @param testMethodName name of the test method
     * @return whether the test should execute in the current envionment
     */
    protected boolean isDisabledInThisEnvironment(String testMethodName) 
    {
        return false;
    }

    public boolean isOffline(String method)
    {
        if (offline)
        {
            System.out.println(StringMessageUtils.getBoilerPlate(
                "Working offline cannot run test: " + method, '=', 80));
        }
        return offline;
    }

    protected final void setUp() throws Exception
    {
        System.out.println(StringMessageUtils.getBoilerPlate("Testing: " + toString(), '=', 80));
        MuleManager.getConfiguration().getDefaultThreadingProfile().setDoThreading(false);
        MuleManager.getConfiguration().setServerUrl(StringUtils.EMPTY);

        try
        {
            if (getTestInfo().getRunCount() == 0)
            {
                if (getTestInfo().isDisposeManagerPerSuite())
                {
                    // We dispose here jut in case
                    disposeManager();
                }
                log("Pre suiteSetup for test: " + getTestInfo());
                suitePreSetUp();
            }
            if (!getTestInfo().isDisposeManagerPerSuite())
            {
                // We dispose here jut in case
                disposeManager();
            }
            doSetUp();
            if (getTestInfo().getRunCount() == 0)
            {
                log("Post suiteSetup for test: " + getTestInfo());
                suitePostSetUp();
            }
        }
        catch (Exception e)
        {
            getTestInfo().incRunCount();
            throw e;
        }
    }

    protected void suitePreSetUp() throws Exception
    {
        // nothing to do
    }

    protected void suitePostSetUp() throws Exception
    {
        // nothing to do
    }

    protected void suitePreTearDown() throws Exception
    {
        // nothing to do
    }

    protected void suitePostTearDown() throws Exception
    {
        // nothing to do
    }

    protected final void tearDown() throws Exception
    {
        try
        {
            if (getTestInfo().getRunCount() == getTestInfo().getTestCount())
            {
                log("Pre suiteTearDown for test: " + getTestInfo());
                suitePreTearDown();
            }
            doTearDown();
            if (!getTestInfo().isDisposeManagerPerSuite())
            {
                disposeManager();
            }
        }
        finally
        {
            getTestInfo().incRunCount();
            if (getTestInfo().getRunCount() == getTestInfo().getTestCount())
            {
                try
                {
                    log("Post suiteTearDown for test: " + getTestInfo());
                    suitePostTearDown();
                }
                finally
                {
                    clearCounter();
                    disposeManager();
                }
            }
        }
    }

    protected void disposeManager() throws UMOException
    {
        log("disposing manager. disposeManagerPerSuite=" + getTestInfo().isDisposeManagerPerSuite());
        if (MuleManager.isInstanciated())
        {
            MuleManager.getInstance().dispose();
        }
        FileUtils.deleteTree(FileUtils.newFile(MuleManager.getConfiguration().getWorkingDirectory()));
        FileUtils.deleteTree(FileUtils.newFile("./ActiveMQ"));
        MuleManager.setConfiguration(new MuleConfiguration());
    }

    protected void doSetUp() throws Exception
    {
        // template method
    }

    protected void doTearDown() throws Exception
    {
        // template method
    }

    public static UMOManager getManager(boolean disableAdminAgent) throws Exception
    {
        return MuleTestUtils.getManager(disableAdminAgent);
    }

    public static UMOModel getDefaultModel() throws UMOException
    {
        return MuleTestUtils.getDefaultModel();
    }

    public static UMOEndpoint getTestEndpoint(String name, String type) throws Exception
    {
        return MuleTestUtils.getTestEndpoint(name, type);
    }

    public static UMOEvent getTestEvent(Object data) throws Exception
    {
        return MuleTestUtils.getTestEvent(data);
    }

    public static UMOEventContext getTestEventContext(Object data) throws Exception
    {
        return MuleTestUtils.getTestEventContext(data);
    }

    public static UMOTransformer getTestTransformer()
    {
        return MuleTestUtils.getTestTransformer();
    }

    public static UMOEvent getTestEvent(Object data, MuleDescriptor descriptor) throws Exception
    {
        return MuleTestUtils.getTestEvent(data, descriptor);
    }

    public static UMOEvent getTestEvent(Object data, UMOImmutableEndpoint endpoint) throws Exception
    {
        return MuleTestUtils.getTestEvent(data, endpoint);
    }

    public static UMOEvent getTestEvent(Object data, MuleDescriptor descriptor, UMOImmutableEndpoint endpoint)
        throws UMOException
    {
        return MuleTestUtils.getTestEvent(data, descriptor, endpoint);
    }

    public static UMOSession getTestSession(UMOComponent component)
    {
        return MuleTestUtils.getTestSession(component);
    }

    public static TestConnector getTestConnector()
    {
        return MuleTestUtils.getTestConnector();
    }

    public static UMOComponent getTestComponent(MuleDescriptor descriptor)
    {
        return MuleTestUtils.getTestComponent(descriptor);
    }

    public static MuleDescriptor getTestDescriptor(String name, String implementation) throws Exception
    {
        return MuleTestUtils.getTestDescriptor(name, implementation);
    }

    public static UMOManager getTestManager() throws Exception
    {
        return MuleTestUtils.getManager(true);
    }

    protected void finalize() throws Throwable
    {
        try
        {
            clearAllCounters();
        }
        finally
        {
            super.finalize();
        }
    }

    protected class TestInfo
    {
        /**
         * Whether to dispose the manager after every method or once all tests for
         * the class have run
         */
        private boolean disposeManagerPerSuite = false;
        private int testCount = 0;
        private int runCount = 0;
        private String name;

        public TestInfo(String name)
        {
            this.name = name;
        }

        public void clearCounts()
        {
            testCount = 0;
            runCount = 0;
            log("Cleared counts for: " + name);
        }

        public void incTestCount()
        {
            testCount++;
            log("Added test: " + name + " " + testCount);
        }

        public void incRunCount()
        {
            runCount++;
            log("Finished Run: " + toString());
        }

        public int getTestCount()
        {
            return testCount;
        }

        public int getRunCount()
        {
            return runCount;
        }

        public String getName()
        {
            return name;
        }

        public boolean isDisposeManagerPerSuite()
        {
            return disposeManagerPerSuite;
        }

        public void setDisposeManagerPerSuite(boolean disposeManagerPerSuite)
        {
            this.disposeManagerPerSuite = disposeManagerPerSuite;
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            return buf.append(name).append(", (").append(runCount).append(" / ").append(testCount).append(
                ") tests run, disposePerSuite=").append(disposeManagerPerSuite).toString();
        }
    }
}
