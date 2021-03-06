/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers;

import org.mule.config.spring.factories.InboundEndpointFactoryBean;
import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.beans.ChildBean;
import org.mule.config.spring.parsers.beans.OrphanBean;
import org.mule.config.spring.parsers.collection.ChildListEntryDefinitionParser;
import org.mule.config.spring.parsers.collection.ChildMapEntryDefinitionParser;
import org.mule.config.spring.parsers.delegate.InheritDefinitionParser;
import org.mule.config.spring.parsers.delegate.MapDefinitionParserMutator;
import org.mule.config.spring.parsers.delegate.SingleParentFamilyDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.generic.NamedDefinitionParser;
import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.mule.config.spring.parsers.generic.ParentDefinitionParser;
import org.mule.config.spring.parsers.specific.ComplexComponentDefinitionParser;
import org.mule.config.spring.parsers.specific.SimplePojoServiceDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportGlobalEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.support.AddressedEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.support.ChildAddressDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.support.ChildEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.support.OrphanEndpointDefinitionParser;
import org.mule.impl.endpoint.EndpointURIEndpointBuilder;

/**
 * Registers a Bean Definition Parser for handling <code><parsers-test:...></code> elements.
 *
 */
public class ParsersTestNamespaceHandler extends AbstractMuleNamespaceHandler
{

    public void init()
    {
        registerBeanDefinitionParser("orphan", new OrphanDefinitionParser(OrphanBean.class, true).addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));
        registerBeanDefinitionParser("child", new ChildDefinitionParser("child", ChildBean.class).addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));
        registerBeanDefinitionParser("mapped-child", new MapDefinitionParserMutator("map", new ChildDefinitionParser("child", ChildBean.class)).addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));
        registerBeanDefinitionParser("kid", new ChildDefinitionParser("kid", ChildBean.class).addAlias("bar", "foo").addIgnored("ignored"));
        registerBeanDefinitionParser("parent", new ParentDefinitionParser().addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));
        registerBeanDefinitionParser("orphan1", new NamedDefinitionParser("orphan1").addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));
        registerBeanDefinitionParser("orphan2", new NamedDefinitionParser("orphan2").addAlias("bar", "foo").addIgnored("ignored"));
        registerBeanDefinitionParser("map-entry", new ChildMapEntryDefinitionParser("map", "key", "value"));
        registerBeanDefinitionParser("list-entry", new ChildListEntryDefinitionParser("list"));
        registerBeanDefinitionParser("named", new NamedDefinitionParser().addAlias("bar", "foo").addIgnored("ignored"));
        registerBeanDefinitionParser("inherit", new InheritDefinitionParser(
                new OrphanDefinitionParser(OrphanBean.class, true),
                new NamedDefinitionParser()).addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring"));

        registerBeanDefinitionParser("address", new ChildAddressDefinitionParser("test").addAlias("address", "host"));
        registerBeanDefinitionParser("orphan-endpoint", new OrphanEndpointDefinitionParser(EndpointURIEndpointBuilder.class));
        registerBeanDefinitionParser("child-endpoint", new ChildEndpointDefinitionParser(InboundEndpointFactoryBean.class));
        registerBeanDefinitionParser("unaddressed-orphan-endpoint", new OrphanEndpointDefinitionParser(EndpointURIEndpointBuilder.class));
        registerBeanDefinitionParser("addressed-orphan-endpoint", new AddressedEndpointDefinitionParser("test", AddressedEndpointDefinitionParser.PROTOCOL, new OrphanEndpointDefinitionParser(EndpointURIEndpointBuilder.class), new String[]{}, new String[]{"path"}));
        registerBeanDefinitionParser("addressed-child-endpoint", new TransportEndpointDefinitionParser("test", InboundEndpointFactoryBean.class, new String[]{}));

        registerBeanDefinitionParser("list-element-test-1", new ChildListEntryDefinitionParser("kids", "listAttribute"));
        registerBeanDefinitionParser("list-element-test-2",
                new SingleParentFamilyDefinitionParser(
                        new OrphanDefinitionParser(OrphanBean.class, true))
                        .addChildDelegate("kid1", new ChildListEntryDefinitionParser("kids", "kid1"))
                        .addChildDelegate("kid2", new ChildListEntryDefinitionParser("kids", "kid2")));
        // simpler list element parser doesn't support dynamic attribute
//        registerBeanDefinitionParser("list-element-test-3", new AllAttributeChildDefinitionParser(new ChildListEntryDefinitionParser("kids")));

        registerBeanDefinitionParser("factory",
                new ComplexComponentDefinitionParser(
                        new SimplePojoServiceDefinitionParser(ChildBean.class, "object"),
                        (ChildDefinitionParser) new ChildDefinitionParser("child", ChildBean.class).addAlias("bar", "foo").addIgnored("ignored").addCollection("offspring")));

        registerBeanDefinitionParser("complex-endpoint",
                new TransportGlobalEndpointDefinitionParser(
                        "test", TransportGlobalEndpointDefinitionParser.PROTOCOL,
                        new String[]{"string", "bar"}, new String[]{"path"}).addAlias("bar", "foo"));

        registerBeanDefinitionParser("no-name", new OrphanDefinitionParser(OrphanBean.class, true));
        registerBeanDefinitionParser("no-name-2", new IndependentDefinitionParser());
    }

}
