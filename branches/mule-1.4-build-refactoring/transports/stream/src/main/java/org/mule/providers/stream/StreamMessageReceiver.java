/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.providers.stream;

import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractPollingMessageReceiver;
import org.mule.umo.UMOComponent;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.provider.UMOConnector;
import org.mule.util.SystemUtils;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PushbackInputStream;

/**
 * <code>StreamMessageReceiver</code> is a listener for events from Mule components
 * which then simply passes the events on to the target components.
 */
public class StreamMessageReceiver extends AbstractPollingMessageReceiver
{
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    private int bufferSize = DEFAULT_BUFFER_SIZE;
    private InputStream inputStream;
    private StreamConnector connector;

    public StreamMessageReceiver(UMOConnector connector,
                                 UMOComponent component,
                                 UMOEndpoint endpoint,
                                 long checkFrequency) throws InitialisationException
    {
        super(connector, component, endpoint);
        this.setFrequency(checkFrequency);

        this.connector = (StreamConnector)connector;
        String streamName = endpoint.getEndpointURI().getAddress();
        if (StreamConnector.STREAM_SYSTEM_IN.equalsIgnoreCase(streamName))
        {
            inputStream = System.in;
        }
        else
        {
            inputStream = this.connector.getInputStream();
        }

        // apply connector-specific properties
        if (connector instanceof SystemStreamConnector)
        {
            SystemStreamConnector ssc = (SystemStreamConnector)connector;

            String promptMessage = (String)endpoint.getProperties().get("promptMessage");
            if (promptMessage != null)
            {
                ssc.setPromptMessage(promptMessage);
            }
        }
    }

    protected void doDispose()
    {
        // template method
    }

    public void doConnect() throws Exception
    {
        if (connector instanceof SystemStreamConnector)
        {
            SystemStreamConnector ssc = (SystemStreamConnector)connector;
            DelayedMessageWriter writer = new DelayedMessageWriter(ssc);
            writer.start();
        }
    }

    public void doDisconnect() throws Exception
    {
        // noop
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.util.timer.TimeEventListener#timeExpired(org.mule.util.timer.TimeEvent)
     */
    public void poll()
    {
        try
        {
            if(endpoint.isStreaming())
            {
                PushbackInputStream in = new PushbackInputStream(inputStream);

                //Block until we have some data
                int i = in.read();
                //Roll back our read
                in.unread(i);
                UMOMessage umoMessage = new MuleMessage(connector.getStreamMessageAdapter(in, null));
                routeMessage(umoMessage, endpoint.isSynchronous());
            }
            else
            {
                byte[] inputBuffer = new byte[bufferSize];
                int len = inputStream.read(inputBuffer);

                if (len == -1)
                {
                    return;
                }

                StringBuffer fullBuffer = new StringBuffer(bufferSize);
                while (len > 0)
                {
                    fullBuffer.append(new String(inputBuffer, 0, len));
                    len = 0; // mark as read
                    if (inputStream.available() > 0)
                    {
                        len = inputStream.read(inputBuffer);
                    }
                }

                // remove any trailing CR/LF
                String finalMessageString;
                int noCRLFLength = fullBuffer.length() - SystemUtils.LINE_SEPARATOR.length();
                if (fullBuffer.indexOf(SystemUtils.LINE_SEPARATOR, noCRLFLength) != -1)
                {
                    finalMessageString = fullBuffer.substring(0, noCRLFLength);
                }
                else
                {
                    finalMessageString = fullBuffer.toString();
                }

                UMOMessage umoMessage = new MuleMessage(connector.getMessageAdapter(finalMessageString));
                routeMessage(umoMessage, endpoint.isSynchronous());
            }

            doConnect();
        }
        catch (Exception e)
        {
            handleException(e);
        }
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public int getBufferSize()
    {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    private class DelayedMessageWriter extends Thread
    {
        private long delay = 0;
        private SystemStreamConnector ssc;

        public DelayedMessageWriter(SystemStreamConnector ssc)
        {
            this.delay = ssc.getMessageDelayTime();
            this.ssc = ssc;
        }

        public void run()
        {
            if (delay > 0)
            {
                try
                {
                    // Allow all other console message to be printed out first
                    sleep(delay);
                }
                catch (InterruptedException e1)
                {
                    // ignore
                }
            }
            ((PrintStream)ssc.getOutputStream()).println();
            ((PrintStream)ssc.getOutputStream()).print(ssc.getPromptMessage());
        }
    }
}
