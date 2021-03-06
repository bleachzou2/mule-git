/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.json.filters;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A filter that will determine if the current message payload is a JSON encoded message.
 */
public class IsJsonFilter implements Filter
{

    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(IsJsonFilter.class);

    private boolean validateParsing = false;

    public IsJsonFilter()
    {
        super();
    }

    public boolean accept(MuleMessage obj)
    {
        return accept(obj.getPayload());
    }

    public boolean accept(Object obj)
    {
        try
        {
            if (obj instanceof String)
            {
                if (!JSONUtils.mayBeJSON((String) obj))
                {
                    return false;
                    //throw new JSONException("Message is not valid JSON");
                }
                if(isValidateParsing()) JSONObject.fromObject(obj);
            }
            else
            {
                //throw new JSONException("Object must be a string");
                return false;
            }
            logger.debug("Filter result = true (message is valid JSON)");
            return true;
        }
        catch (JSONException e)
        {
            logger.debug("Filter result = false (message is not valid JSON): " + e.getMessage());
            return false;
        }
    }

    public boolean isValidateParsing()
    {
        return validateParsing;
    }

    public void setValidateParsing(boolean validateParsing)
    {
        this.validateParsing = validateParsing;
    }
}
