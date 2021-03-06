/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.http.functional;

import org.mule.MuleManager;
import org.mule.MuleServer;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.umo.UMOEventContext;
import org.mule.umo.manager.UMOManager;
import org.mule.umo.model.UMOModel;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

public class PollingReceiversRestartTestCase extends AbstractMuleTestCase
{
    private static final int WAIT_TIME = 2500;

    public void testPollingReceiversRestart() throws Exception
    {
        // we are going to stop and start Mule in this thread, make it
        // a daemon so the test can exit properly
        MuleServer mule = new MuleServer("polling-receivers-restart-test.xml");
        mule.start(true);

        // well, no way to register notification on the MuleServer, only
        // possible for MuleManager, so just sleep
        Thread.sleep(WAIT_TIME);

        UMOManager manager = MuleManager.getInstance();

        UMOModel model = (UMOModel) MuleManager.getInstance().getModels().get("PollingHttpRestart");
        FunctionalTestComponent ftc = (FunctionalTestComponent) model.getComponent("Test").getInstance();
        assertNotNull("Functional Test Component not found in the model.", ftc);

        AtomicInteger pollCounter = new AtomicInteger(0);
        ftc.setEventCallback(new PollingEventCallback(pollCounter));

        // should be enough to poll for 2 messages
        Thread.sleep(WAIT_TIME);

        // stop
        manager.stop();
        assertTrue("No polls performed", pollCounter.get() > 0);

        // and restart
        manager.start();

        pollCounter.set(0);
        ftc.setEventCallback(new PollingEventCallback(pollCounter));

        Thread.sleep(WAIT_TIME);
        manager.dispose();
        assertTrue("No polls performed", pollCounter.get() > 0);
    }

    private static class PollingEventCallback implements EventCallback
    {
        private final AtomicInteger pollCounter;

        public PollingEventCallback(final AtomicInteger pollCounter)
        {
            this.pollCounter = pollCounter;
        }

        public void eventReceived(final UMOEventContext context, final Object component) throws Exception
        {
            pollCounter.incrementAndGet();
            String message = context.getMessageAsString();
            assertEquals("Here's your content.", message);
        }
    }
}

