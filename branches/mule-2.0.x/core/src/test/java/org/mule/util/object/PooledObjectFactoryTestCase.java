/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.util.object;

import org.mule.api.service.Service;
import org.mule.config.PoolingProfile;
import org.mule.model.seda.SedaModel;
import org.mule.model.seda.SedaService;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.services.UniqueComponent;
import org.mule.tck.testmodels.fruit.Orange;
import org.mule.tck.testmodels.fruit.WaterMelon;
import org.mule.util.ExceptionUtils;
import org.mule.util.UUID;

public class PooledObjectFactoryTestCase extends AbstractMuleTestCase
{
    public static final byte MAX_ACTIVE = 3;
    public static final long MAX_WAIT = 1500;

    // @Override
    public void testInitialise()
    {
        // TODO HH: auto-generated method stub
    }

    // @Override
    public void testDispose()
    {
        // TODO HH: auto-generated method stub
    }

    // @Override
    public void testGet()
    {
        // TODO HH: auto-generated method stub
    }

    // @Override
    public void testGetObjectClass()
    {
        // TODO HH: auto-generated method stub
    }

    // @Override
    public void testRelease()
    {
        // TODO HH: auto-generated method stub
    }

    protected PoolingProfile getDefaultPoolingProfile()
    {
        PoolingProfile pp = new PoolingProfile();
        pp.setMaxActive(MAX_ACTIVE);
        pp.setMaxWait(MAX_WAIT);
        pp.setInitialisationPolicy(PoolingProfile.INITIALISE_NONE);

        return pp;
    }

    public void testCreatePool() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_FAIL);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        assertEquals(0, of.getPoolSize());

        Object borrowed = of.getOrCreate();
        assertNotNull(borrowed);
        assertEquals(1, of.getPoolSize());
        of.release(borrowed);
        assertEquals(0, of.getPoolSize());

        borrowed = of.getOrCreate();
        assertNotNull(borrowed);
        assertEquals(1, of.getPoolSize());
        Object borrowed2 = of.getOrCreate();
        assertNotNull(borrowed2);
        assertEquals(2, of.getPoolSize());
    }

    public void testFailOnExhaust() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_WAIT);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        Object borrowed = null;

        for (int i = 0; i < MAX_ACTIVE; i++)
        {
            borrowed = of.getOrCreate();
            assertNotNull(borrowed);
            assertEquals(of.getPoolSize(), i + 1);
        }

        try
        {
            borrowed = of.getOrCreate();
            fail("Should throw an Exception");
        }
        catch (Exception e)
        {
            // expected
        }
    }

    public void testBlockExpiryOnExhaust() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_WAIT);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        Object borrowed = null;

        assertEquals(0, of.getPoolSize());
        borrowed = of.getOrCreate();
        assertNotNull(borrowed);
        borrowed = of.getOrCreate();
        assertNotNull(borrowed);
        borrowed = of.getOrCreate();
        assertNotNull(borrowed);
        assertEquals(3, of.getPoolSize());

        // TODO
        // long starttime = System.currentTimeMillis();
        try
        {
            borrowed = of.getOrCreate();
            fail("Should throw an Exception");
        }
        catch (Exception e)
        {
            // TODO
            // long totalTime = System.currentTimeMillis() - starttime;
            // Need to allow for alittle variance in system time
            // This is unreliable
            // assertTrue(totalTime < (DEFAULT_WAIT + 300) && totalTime >
            // (DEFAULT_WAIT - 300));
        }
    }

    public void testBlockOnExhaust() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_WAIT);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        Object borrowed = null;

        assertEquals(0, of.getPoolSize());

        borrowed = of.getOrCreate();
        borrowed = of.getOrCreate();
        assertEquals(2, of.getPoolSize());

        // TODO
        // long starttime = System.currentTimeMillis();
        long borrowerWait = 500;
        Borrower borrower = new Borrower(of, borrowerWait);
        borrower.start();
        // Make sure the borrower borrows first
        try
        {
            Thread.sleep(200);
        }
        catch (InterruptedException e)
        {
            // ignore
        }

        borrowed = of.getOrCreate();
        // TODO
        // long totalTime = System.currentTimeMillis() - starttime;
        // Need to allow for alittle variance in system time
        // This is unreliable
        // assertTrue(totalTime < (borrowerWait + 300) && totalTime >
        // (borrowerWait -300));

        assertNotNull(borrowed);
    }

    public void testGrowOnExhaust() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_GROW);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        Object borrowed = of.getOrCreate();
        borrowed = of.getOrCreate();
        borrowed = of.getOrCreate();
        assertEquals(3, of.getPoolSize());
        // assertEquals(3, pool.getMaxSize());

        // Should now grow
        borrowed = of.getOrCreate();
        assertNotNull(borrowed);

        assertEquals(4, of.getPoolSize());
    }

    public void testClearPool() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_FAIL);
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, pp);
        of.initialise();

        Object borrowed = of.getOrCreate();
        assertEquals(1, of.getPoolSize());
        of.release(borrowed);

        of.dispose();
        assertEquals(0, of.getPoolSize());

        of.initialise();
        borrowed = of.getOrCreate();
        assertEquals(1, of.getPoolSize());
    }

    public void testObjectUniqueness() throws Exception
    {
        PoolingProfile pp = getDefaultPoolingProfile();
        pp.setExhaustedAction(PoolingProfile.WHEN_EXHAUSTED_FAIL);
        PooledObjectFactory of = new PooledObjectFactory(UniqueComponent.class, pp);
        of.initialise();

        assertEquals(0, of.getPoolSize());

        Object obj;

        obj = of.getOrCreate();
        assertNotNull(obj);
        assertTrue("Object should be of type UniqueComponent", obj instanceof UniqueComponent);
        String id1 = ((UniqueComponent) obj).getId();
        assertNotNull(id1);

        obj = of.getOrCreate();
        assertNotNull(obj);
        assertTrue("Object should be of type UniqueComponent", obj instanceof UniqueComponent);
        String id2 = ((UniqueComponent) obj).getId();
        assertNotNull(id2);

        obj = of.getOrCreate();
        assertNotNull(obj);
        assertTrue("Object should be of type UniqueComponent", obj instanceof UniqueComponent);
        String id3 = ((UniqueComponent) obj).getId();
        assertNotNull(id3);

        assertFalse("Service IDs " + id1 + " and " + id2 + " should be different", id1.equals(id2));
        assertFalse("Service IDs " + id2 + " and " + id3 + " should be different", id2.equals(id3));
    }

    public void testOnRemoveCallsDispose() throws Exception
    {
        PooledObjectFactory of = new PooledObjectFactory(WaterMelon.class, getDefaultPoolingProfile());
        of.initialise();

        WaterMelon wm = (WaterMelon) of.getOrCreate();
        of.release(wm);
        assertEquals("disposed", wm.getState());
    }

    public void testWithinComponent() throws Exception
    {
        SedaModel model = new SedaModel();
        model.setMuleContext(muleContext);
        muleContext.applyLifecycle(model);

        Service c = new SedaService();
        c.setName("test");
        PooledObjectFactory of = new PooledObjectFactory(Orange.class, getDefaultPoolingProfile());
        of.initialise();
        c.setServiceFactory(of);
        c.setModel(model);

        c.setMuleContext(muleContext);
        muleContext.applyLifecycle(c);

        assertTrue(c.getServiceFactory() instanceof PooledObjectFactory);
        assertEquals(0, ((PooledObjectFactory) c.getServiceFactory()).getPoolSize());
        assertTrue(c.getServiceFactory().getOrCreate() instanceof Orange);
        assertEquals(1, ((PooledObjectFactory) c.getServiceFactory()).getPoolSize());
        c.dispose();
        assertEquals(0, ((PooledObjectFactory) c.getServiceFactory()).getPoolSize());
    }

    public void testLifeCycleMethods() throws Exception
    {
        PooledObjectFactory of = new PooledObjectFactory(UniqueComponent.class, getDefaultPoolingProfile());
        of.initialise();

        String id = UUID.getUUID();
        Object obj = of.makeObject(id);
        assertNotNull(obj);
        assertTrue(of.validateObject(id, obj));
        of.activateObject(id, obj);
        of.passivateObject(id, obj);
        of.destroyObject(id, obj);
    }

    private class Borrower extends Thread
    {
        private PooledObjectFactory of;
        private long time;

        public Borrower(PooledObjectFactory of, long time)
        {
            super("Borrower");
            if (of == null)
            {
                throw new IllegalArgumentException("Pool cannot be null");
            }
            this.of = of;
            if (time < 500)
            {
                time = 500;
            }
            this.time = time;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                Object object = of.getOrCreate();
                try
                {
                    sleep(time);
                }
                catch (InterruptedException e)
                {
                    // ignore
                }
                of.release(object);
            }
            catch (Exception e)
            {
                fail("Borrower thread failed:\n" + ExceptionUtils.getStackTrace(e));
            }
        }

    }

}
