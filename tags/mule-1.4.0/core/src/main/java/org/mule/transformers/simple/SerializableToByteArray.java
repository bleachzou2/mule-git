/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transformers.simple;

import org.mule.transformers.AbstractEventAwareTransformer;
import org.mule.umo.UMOEventContext;
import org.mule.umo.UMOMessage;
import org.mule.umo.transformer.TransformerException;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;

/**
 * <code>SerializableToByteArray</code> converts a serializable object or a String
 * to a byte array. If <code>UMOMessage</code> is configured as a source type on this
 * transformer by calling <code>setAcceptUMOMessage(true)</code> then the UMOMessage
 * will be serialised. This is useful for transports such as TCP where the message
 * headers would normally be lost.
 */
public class SerializableToByteArray extends AbstractEventAwareTransformer
{

    public SerializableToByteArray()
    {
        this.registerSourceType(Serializable.class);
        this.registerSourceType(byte[].class);
        this.setReturnClass(byte[].class);
    }

    public boolean isAcceptUMOMessage()
    {
        return this.isSourceTypeSupported(UMOMessage.class, true);
    }

    public void setAcceptUMOMessage(boolean value)
    {
        if (value)
        {
            this.registerSourceType(UMOMessage.class);
        }
        else
        {
            this.unregisterSourceType(UMOMessage.class);
        }
    }

    public Object transform(Object src, String encoding, UMOEventContext context) throws TransformerException
    {
        /*
         * If the UMOMessage source type has been registered then we can assume that
         * the whole message is to be serialised, not just the payload. This can be
         * useful for protocols such as tcp where the protocol does not support
         * headers and the whole message needs to be serialized.
         */
        if (this.isAcceptUMOMessage())
        {
            src = context.getMessage();
        }
        else if (src instanceof byte[])
        {
            return src;
        }

        try
        {
            return SerializationUtils.serialize((Serializable) src);
        }
        catch (Exception e)
        {
            throw new TransformerException(this, e);
        }
    }

}
