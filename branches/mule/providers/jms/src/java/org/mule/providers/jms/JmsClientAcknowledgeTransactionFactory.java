/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) Cubis Limited. All rights reserved.
 * http://www.cubis.co.uk
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.providers.jms;

import org.mule.transaction.IllegalTransactionStateException;
import org.mule.umo.UMOTransaction;
import org.mule.umo.UMOTransactionException;
import org.mule.umo.UMOTransactionFactory;

import javax.jms.Message;

/**
 * <code>JmsClientAcknowledgeTransactionFactory</code> creates a Jms Client Acknowledge transaction
 * using a Jms message.
 *
 * @author <a href="mailto:ross.mason@cubis.co.uk">Ross Mason</a>
 * @version $Revision$
 */

public class JmsClientAcknowledgeTransactionFactory implements UMOTransactionFactory
{
    /* (non-Javadoc)
     * @see org.mule.umo.UMOTransactionFactory#beginTransaction(org.mule.umo.provider.UMOMessageDispatcher)
     */
    public UMOTransaction beginTransaction(Object message) throws UMOTransactionException
    {
        if (message instanceof Message)
        {
            JmsClientAcknowledgeTransaction tx = new JmsClientAcknowledgeTransaction((Message) message);
            tx.begin();
            return tx;
        }
        else
        {
            throw new IllegalTransactionStateException("Argument was not of expected type: " + Message.class.getName());
        }
    }

    public boolean isTransacted()
    {
        return false;
    }
}