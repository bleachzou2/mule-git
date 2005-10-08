/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.impl.endpoint;

import java.net.URI;
import java.net.URLDecoder;
import java.util.Properties;
import java.io.UnsupportedEncodingException;

import org.mule.providers.service.ConnectorFactory;
import org.mule.umo.endpoint.MalformedEndpointException;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.util.PropertiesHelper;
import org.mule.util.SgmlCodec;
import org.mule.MuleManager;

/**
 * <code>UrlEndpointBuilder</code> is the default endpointUri strategy
 * suitable for most connectors
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision$
 */

public abstract class AbstractEndpointBuilder implements EndpointBuilder
{
    public static final String PROPERTY_ENDPOINT_NAME = "endpointName";
    public static final String PROPERTY_ENDPOINT_URI = "address";
    public static final String PROPERTY_CREATE_CONNECTOR = "createConnector";
    public static final String PROPERTY_CONNECTOR_NAME = "connector";
    public static final String PROPERTY_TRANSFORMERS = "transformers";
    protected String address;
    protected String endpointName;
    protected String connectorName;
    protected String transformers;
    protected String userInfo;


    protected int createConnector = ConnectorFactory.GET_OR_CREATE_CONNECTOR;

    public UMOEndpointURI build(URI uri) throws MalformedEndpointException
    {
        Properties props = getPropertiesForURI(uri);
        if(address==null) {
            setEndpoint(uri, props);
        }

        UMOEndpointURI ep = new MuleEndpointURI(address,
                                                endpointName,
                                                connectorName,
                                                transformers,
                                                createConnector,
                                                props,
                                                uri,
                                                userInfo);
        address = null;
        endpointName = null;
        connectorName = null;
        transformers = null;
        createConnector = ConnectorFactory.GET_OR_CREATE_CONNECTOR;
        return ep;
    }

    protected abstract void setEndpoint(URI uri, Properties props) throws MalformedEndpointException;

    protected Properties getPropertiesForURI(URI uri) throws MalformedEndpointException {
        Properties properties = PropertiesHelper.getPropertiesFromQueryString(uri.getQuery());

        String tempEndpointName = (String) properties.get(PROPERTY_ENDPOINT_NAME);
        if (tempEndpointName != null) {
            this.endpointName = tempEndpointName;
        }
        // override the endpointUri if set
        String endpoint = (String) properties.get(PROPERTY_ENDPOINT_URI);
        if (endpoint != null) {
            this.address = endpoint;
            address = decode(address, uri);
        }

        String cnnName = (String) properties.get(PROPERTY_CONNECTOR_NAME);
        if (cnnName != null) {
            this.connectorName = cnnName;
        }

        String create = (String) properties.get(PROPERTY_CREATE_CONNECTOR);
        if (create != null) {
            if ("0".equals(create)) {
                this.createConnector = ConnectorFactory.GET_OR_CREATE_CONNECTOR;
            } else if ("1".equals(create)) {
                this.createConnector = ConnectorFactory.ALWAYS_CREATE_CONNECTOR;
            } else if ("2".equals(create)) {
                this.createConnector = ConnectorFactory.NEVER_CREATE_CONNECTOR;
            } else if ("IF_NEEDED".equals(create)) {
                this.createConnector = ConnectorFactory.GET_OR_CREATE_CONNECTOR;
            } else if ("ALWAYS".equals(create)) {
                this.createConnector = ConnectorFactory.ALWAYS_CREATE_CONNECTOR;
            } else if ("NEVER".equals(create)) {
                this.createConnector = ConnectorFactory.NEVER_CREATE_CONNECTOR;
            } else if (connectorName == null) {
                this.createConnector = ConnectorFactory.USE_CONNECTOR;
                connectorName = create;
            }

        }

        transformers = (String) properties.get(PROPERTY_TRANSFORMERS);
        if (transformers != null) {
            transformers = transformers.replaceAll(" ", ",");
        }
        //If we have user info, decode it as it might contain '@' or other encodable characters
        userInfo = uri.getUserInfo();
        if(userInfo!=null) {
            userInfo = decode(userInfo, uri);
        }
        return properties;
    }

    private String decode(String string, URI uri) throws MalformedEndpointException {
        try {
            return URLDecoder.decode(string, MuleManager.getConfiguration().getEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new MalformedEndpointException(uri.toString(), e);
        }
    }
}
