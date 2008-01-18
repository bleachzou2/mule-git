/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.http.config;



import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.collection.ChildListEntryDefinitionParser;
import org.mule.config.spring.parsers.collection.ChildMapEntryDefinitionParser;
import org.mule.config.spring.parsers.generic.MuleOrphanDefinitionParser;
import org.mule.config.spring.parsers.specific.ComplexComponentDefinitionParser;
import org.mule.impl.endpoint.URIBuilder;
import org.mule.providers.http.HttpConnector;
import org.mule.providers.http.HttpConstants;
import org.mule.providers.http.components.RestServiceWrapper;
import org.mule.providers.http.transformers.HttpClientMethodResponseToObject;
import org.mule.providers.http.transformers.HttpResponseToString;
import org.mule.providers.http.transformers.ObjectToHttpClientMethodRequest;
import org.mule.providers.http.transformers.UMOMessageToHttpResponse;

/**
 * Reigsters a Bean Definition Parser for handling <code><http:connector></code> elements.
 */
public class HttpNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
        registerStandardTransportEndpoints(HttpConnector.HTTP, URIBuilder.SOCKET_ATTRIBUTES)
                .addAlias("contentType", HttpConstants.HEADER_CONTENT_TYPE);
        
        registerBeanDefinitionParser("connector", new MuleOrphanDefinitionParser(HttpConnector.class, true));

        registerBeanDefinitionParser("rest-service-component", new ComplexComponentDefinitionParser(RestServiceWrapper.class));
        registerBeanDefinitionParser("payloadParameterName", new ChildListEntryDefinitionParser("payloadParameterNames", ChildMapEntryDefinitionParser.VALUE));
        registerBeanDefinitionParser("requiredParameter", new ChildMapEntryDefinitionParser("requiredParams"));
        registerBeanDefinitionParser("optionalParameter", new ChildMapEntryDefinitionParser("optionalParams"));
        
        registerBeanDefinitionParser("http-client-response-to-object-transformer", new MuleOrphanDefinitionParser(HttpClientMethodResponseToObject.class, false));
        registerBeanDefinitionParser("http-response-to-string-transformer", new MuleOrphanDefinitionParser(HttpResponseToString.class, false));
        registerBeanDefinitionParser("object-to-http-client-request-transformer", new MuleOrphanDefinitionParser(ObjectToHttpClientMethodRequest.class, false));
        registerBeanDefinitionParser("message-to-http-response-transformer", new MuleOrphanDefinitionParser(UMOMessageToHttpResponse.class, false));
    }
}
