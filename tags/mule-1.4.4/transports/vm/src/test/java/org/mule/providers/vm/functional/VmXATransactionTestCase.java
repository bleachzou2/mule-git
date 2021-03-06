/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.vm.functional;

import org.mule.extras.client.MuleClient;
import org.mule.tck.FunctionalTestCase;
import org.mule.transaction.TransactionCoordination;
import org.mule.transaction.XaTransaction;
import org.mule.umo.UMOMessage;
import org.mule.umo.UMOTransaction;
import org.mule.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VmXATransactionTestCase extends FunctionalTestCase
{
    protected static final Log logger = LogFactory.getLog(VmTransactionTestCase.class);
    protected static volatile boolean success = true;
    protected static volatile boolean wouldBeDisabled = false;

    protected String getConfigResources()
    {
        return "vm-xa-transaction.xml";
    }

    public void testTransactionQueueEventsTrue() throws Exception
    {

        MuleClient client = new MuleClient();
        client.dispatch("vm://in", "TEST", null);
        UMOMessage message = client.receive("vm://out", 10000);
        assertNotNull(message);
        if (wouldBeDisabled)
        {
            throw new IllegalStateException("Test is wrong, and must be disabled");
        }
        assertTrue(success);

    }

    public static class TestComponent
    {

        public Object process(Object a) throws Exception
        {
            success = checkTransaction(TransactionCoordination.getInstance().getTransaction());
            return a;
        }

        private boolean checkTransaction(UMOTransaction transaction) throws Exception
        {
            if (!(transaction instanceof XaTransaction))
            {
                return false;
            }
            Transaction tx = ((XaTransaction) transaction).getTransaction();
            //add test resource
            TestResource testResource = new TestResource();
            transaction.bindResource("TestReource", testResource);
            tx.enlistResource(testResource);

            Field field = transaction.getClass().getDeclaredField("resources");
            field.setAccessible(true);
            Map resources = (Map) field.get(transaction);
            if (resources == null)
            {
                return false;
            }
            logger.debug("Mule XATransaction :: registered " + resources.size());

            Iterator i = resources.entrySet().iterator();
            boolean result = true;
            while (i.hasNext())
            {
                Map.Entry entry = (Map.Entry) i.next();
                XAResource xaResource = getXAResource(entry.getValue());
                logger.debug("XAResource " + xaResource);
                boolean enlisted = isXAResourceEnlisted(tx, xaResource);
                result = result && enlisted;
                if (xaResource instanceof TestResource)
                {
                    logger.debug("Check status for TestResource " + enlisted);
                    if (!enlisted)
                    {
                        wouldBeDisabled = true;
                        throw new IllegalStateException("Test is wrong, and must be disabled");
                    }
                }
            }

            return result;
        }

        private boolean isXAResourceEnlisted(Transaction transaction, XAResource xaResource)
        {
            if (transaction instanceof
                    com.arjuna.ats.jta.transaction.Transaction)
            {
                com.arjuna.ats.jta.transaction.Transaction tx = (com.arjuna.ats.jta.transaction.Transaction) transaction;

                int state = tx.getXAResourceState(xaResource);
                return (state == 0);
            }
            return false;
        }

        private XAResource getXAResource(Object resource) throws Exception
        {
            if (resource instanceof XAResource)
            {
                return (XAResource) resource;
            }
            Method method = resource.getClass().getMethod("getXAResource", null);
            return (XAResource) method.invoke(resource, ClassUtils.NO_ARGS);
        }


    }

    public static class TestResource implements XAResource
    {

        public void commit(Xid id, boolean onePhase) throws XAException
        {
            logger.debug("XA_COMMIT[" + id + "]");
        }

        public void end(Xid xid, int flags) throws XAException
        {
            logger.debug("XA_END[" + xid + "] Flags=" + flags);
        }

        public void forget(Xid xid) throws XAException
        {
            logger.debug("XA_FORGET[" + xid + "]");
        }

        public int getTransactionTimeout() throws XAException
        {
            return (_timeout);
        }

        public boolean isSameRM(XAResource xares) throws XAException
        {
            return (xares.equals(this));
        }

        public int prepare(Xid xid) throws XAException
        {
            logger.debug("XA_PREPARE[" + xid + "]");

            return (XA_OK);
        }

        public Xid[] recover(int flag) throws XAException
        {
            logger.debug("RECOVER[" + flag + "]");
            return (null);
        }

        public void rollback(Xid xid) throws XAException
        {
            logger.debug("XA_ROLLBACK[" + xid + "]");
        }

        public boolean setTransactionTimeout(int seconds) throws XAException
        {
            _timeout = seconds;
            return (true);
        }

        public void start(Xid xid, int flags) throws XAException
        {
            logger.debug("XA_START[" + xid + "] Flags=" + flags);
        }

        protected int _timeout = 0;
    }


}
