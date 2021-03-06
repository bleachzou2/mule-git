/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.converters;

import org.mule.RegistryContext;
import org.mule.impl.endpoint.MuleEndpointURI;
import org.mule.umo.endpoint.UMOEndpoint;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * <code>EndpointConverter</code> TODO
 */
public class EndpointConverter implements Converter
{

    // --------------------------------------------------------- Public Methods

    /**
     * Convert the specified input object into an output object of the specified
     * type.
     * 
     * @param type Data type to which this value should be converted
     * @param value The input value to be converted
     * @throws ConversionException if conversion cannot be performed successfully
     */
    public Object convert(Class type, Object value)
    {
        if (value == null)
        {
            throw new ConversionException("No value specified");
        }
        if (value instanceof UMOEndpoint)
        {
            return (value);
        }
        try
        {
            String endpointString = value.toString();
            UMOImmutableEndpoint globalEndpoint = (UMOImmutableEndpoint)RegistryContext.getRegistry().getEndpoints().get(
                endpointString);
            if (globalEndpoint == null)
            {
                UMOEndpointURI endpointUri = new MuleEndpointURI(endpointString);
                if (!endpointString.equals(value.toString()))
                {
                    endpointUri.setEndpointName(value.toString());
                }
                UMOEndpoint endpoint = RegistryContext.getRegistry().createEndpointFromUri(endpointUri, UMOImmutableEndpoint.ENDPOINT_TYPE_SENDER_AND_RECEIVER);

                // If the value was an endpoint identifier reference then set
                // the
                // reference as the name of the endpoint
                if (endpointUri.getEndpointName() == null && !endpointString.equals(value.toString()))
                {
                    endpoint.setName(value.toString());
                }
                return endpoint;
            }
            else
            {
                // Global endpoints are late bound to objects because they are
                // cloned by
                // the Mule Manager. So e return null here and the endpoint will
                // be set later
                return null;
            }
        }
        catch (Exception e)
        {
            throw new ConversionException(e);
        }
    }
}
