/*
 * $Header: /opt/cvsroot/mule2/mule-core/src/main/java/org/mule/spring/config/AbstractNamedSingleBeanDefinitionParser.java,v 1.1 2006/02/01 19:42:11 rossmason Exp $
 * $Revision: 1.1 $
 * $Date: 2006/02/01 19:42:11 $
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.extras.spring.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.util.Properties;

/**
 * todo document
 *
 */
public abstract class AbstractMuleSingleBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser
{

    //public static final String ID_ATTRIBUTE = "id";
    //public static final String LOCAL_NAMESPACE = "http://mule.mulesource.org/schema/";

    //Make the registry available to the parsers
    protected BeanDefinitionRegistry registry;
    protected Properties attributeMappings = new Properties();


    protected String extractPropertyName(String attributeName)
    {
        attributeName = getAttributeMapping(attributeName);
        return super.extractPropertyName(attributeName);
    }

    protected abstract Class getBeanClass(Element element);

    protected void postProcess(RootBeanDefinition beanDefinition, Element element) {

    }

    protected void registerAttributeMapping(String alias, String propertyName) {
        attributeMappings.put(alias, propertyName);
    }

    protected String getAttributeMapping(String alias) {
        return attributeMappings.getProperty(alias, alias);
    }
}