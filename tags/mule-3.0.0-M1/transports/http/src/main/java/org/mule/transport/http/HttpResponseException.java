/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

/**
 * A wrapper exceptin for any http client return codes over the 400 range
 */
public class HttpResponseException extends Exception
{
    private String responseText;
    private int responseCode;

    public HttpResponseException(String responseText, int responseCode)
    {
        super(responseText + ", code: " + responseCode);
        this.responseCode = responseCode;
        this.responseText = responseText;
    }

    public String getResponseText()
    {
        return responseText;
    }

    public int getResponseCode()
    {
        return responseCode;
    }
}
