/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extras.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * <code>MuleApplicationContext</code> is A Simple extension Application
 * context that allows rosurces to be loaded from the Classpath of file system
 * using the MuleBeanDefinitionReader.
 * 
 * @see MuleBeanDefinitionReader
 * 
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision: 2179 $
 */
public class MuleApplicationContext extends AbstractXmlApplicationContext
{
    private String[] configLocations;

    public MuleApplicationContext(String configLocation)
    {
        this(new String[] { configLocation });
    }

    public MuleApplicationContext(String[] configLocations)
    {
        this(configLocations, true);
    }

    public MuleApplicationContext(String[] configLocations, boolean refresh) throws BeansException
    {
        this.configLocations = configLocations;
        if (refresh) {
            refresh();
        }
    }

    protected String[] getConfigLocations()
    {
        return configLocations;
    }

    protected Resource getResourceByPath(String path)
    {
        String filePath = path;
        if (filePath != null && filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        File file = new File(filePath);
        if (file.exists()) {
            return new FileSystemResource(filePath);
        } else {
            return super.getResourceByPath(path);
        }
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader)
    {
        //xmlBeanDefinitionReader.setValidationMode(false);
        super.initBeanDefinitionReader(xmlBeanDefinitionReader);
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException
    {
        XmlBeanDefinitionReader beanDefinitionReader = new MuleBeanDefinitionReader(beanFactory, configLocations.length);
        initBeanDefinitionReader(beanDefinitionReader);
        loadBeanDefinitions(beanDefinitionReader);
    }


    /**
     * Create the Mule bean factory for this context.
     *
     * @return the bean factory for this context
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
     * @see #getInternalParentBeanFactory
     */
    protected DefaultListableBeanFactory createBeanFactory() {
        return new MuleBeanFactory(getInternalParentBeanFactory());
    }
}
