/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.tcp;

import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOImmutableEndpoint;
import org.mule.umo.transformer.TransformerException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Send transformed Mule events over TCP.
 */
public class TcpMessageDispatcher extends AbstractMessageDispatcher
{

    private final TcpConnector connector;

    public TcpMessageDispatcher(UMOImmutableEndpoint endpoint)
    {
        super(endpoint);
        this.connector = (TcpConnector) endpoint.getConnector();
    }

    protected synchronized void doDispatch(UMOEvent event) throws Exception
    {
        Socket socket = connector.getSocket(event.getEndpoint());
        try 
        {
            dispatchToSocket(socket, event);
        }
        finally 
        {
            connector.releaseSocket(socket, event.getEndpoint());
        }
    }

    protected synchronized UMOMessage doSend(UMOEvent event) throws Exception
    {
        Socket socket = connector.getSocket(event.getEndpoint());
        dispatchToSocket(socket, event);

        try 
        {
            if (useRemoteSync(event))
            {
                try
                {
                    Object result = receiveFromSocket(socket, event.getTimeout(), endpoint);
                    if (result == null)
                    {
                        return null;
                    }
                    
                    if (result instanceof UMOMessage)
                    {
                    	return (UMOMessage) result;
                    }
                    
                    return new MuleMessage(connector.getMessageAdapter(result));
                }
                catch (SocketTimeoutException e)
                {
                    // we don't necessarily expect to receive a response here
                    logger.info("Socket timed out normally while doing a synchronous receive on endpointUri: "
                        + event.getEndpoint().getEndpointURI());
                    return null;
                }
            }
            else
            {
                return event.getMessage();
            }
        }
        finally
        {
            if (!useRemoteSync(event))
            {
                connector.releaseSocket(socket, endpoint);
            }
        }
        
    }

    // Socket management (get and release) is handled outside this method
    private void dispatchToSocket(Socket socket, UMOEvent event) throws Exception
    {
        Object payload = event.getTransformedMessage();
        write(socket, payload);
    }

    private void write(Socket socket, Object data) throws IOException, TransformerException
    {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        connector.getTcpProtocol().write(bos, data);
        bos.flush();
    }

    protected static Object receiveFromSocket(final Socket socket, int timeout, final UMOImmutableEndpoint endpoint)
            throws IOException
    {
        final TcpConnector connector = (TcpConnector) endpoint.getConnector();
        DataInputStream underlyingIs = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        TcpInputStream tis = new TcpInputStream(underlyingIs)
        {
            public void close() throws IOException
            {
                try
                {
                    connector.releaseSocket(socket, endpoint);
                }
                catch (IOException e)
                {
                   throw e;
                }
                catch (Exception e)
                {
                    IOException e2 = new IOException();
                    e2.initCause(e);
                    throw e2;
                }
            }

        };

        if (timeout >= 0)
        {
            socket.setSoTimeout(timeout);
        }

        try
        {
            return connector.getTcpProtocol().read(tis);
        }
        finally
        {
            if (!tis.isStreaming())
            {
                tis.close();
            }
        }
    }

    protected synchronized void doDispose()
    {
        try
        {
            doDisconnect();
        }
        catch (Exception e)
        {
            logger.error("Failed to shutdown the dispatcher.", e);
        }
    }

    protected void doConnect() throws Exception
    {
        // Test the connection
        if (connector.isValidateConnections())
        {
            Socket socket = connector.getSocket(endpoint);
            connector.releaseSocket(socket, endpoint);
        }
    }

    protected void doDisconnect() throws Exception
    {
        //nothing to do
    }
    
}
