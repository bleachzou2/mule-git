/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the BSD style
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.registry;

import org.mule.RegistryContext;
import org.mule.config.MuleProperties;
import org.mule.config.i18n.CoreMessages;
import org.mule.impl.model.DefaultModelServiceDescriptor;
import org.mule.providers.service.DefaultTransportServiceDescriptor;
import org.mule.providers.service.TransportServiceDescriptor;
import org.mule.util.ClassUtils;
import org.mule.util.SpiUtils;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;

/**
 * Factory used to create a new service descriptor.
 */
public class ServiceDescriptorFactory 
{
    // Service types (used for looking up the service descriptors)
    public static final String PROVIDER_SERVICE_TYPE = "transport";
    public static final String MODEL_SERVICE_TYPE = "model";
    public static final String EXCEPTION_SERVICE_TYPE = "exception";
    
    /**
     * Factory method to create a new service descriptor.
     */
    public static ServiceDescriptor create(String type, String name, Properties props, Properties overrides, ApplicationContext context) throws ServiceException
    {       
        String serviceFinderClass = null;
        if(overrides!=null)
        {
            serviceFinderClass = (String) props.remove(MuleProperties.SERVICE_FINDER);
            props.putAll(overrides);
        }
        
        ServiceDescriptor sd = null;
        if (type.equals(PROVIDER_SERVICE_TYPE)) 
        {
            try
            {
                sd = new DefaultTransportServiceDescriptor(name, props, context);
            }
            catch (ClassNotFoundException e)
            {
                //TODO RM* e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            props = SpiUtils.findServiceDescriptor(EXCEPTION_SERVICE_TYPE, name + "-exception-mappings");
            ((TransportServiceDescriptor) sd).setExceptionMappings(props);
        }
        else if (type.equals(MODEL_SERVICE_TYPE))
        {
            sd = new DefaultModelServiceDescriptor(name, props);
        }
        else 
        {
            throw new ServiceException(CoreMessages.unrecognisedServiceType(type));
        }
        
        // If there is a finder service, use it to find the "real" service.
        if (StringUtils.isNotBlank(serviceFinderClass))
        {
            ServiceFinder finder;
            try
            {
                finder = (ServiceFinder)ClassUtils.instanciateClass(serviceFinderClass, ClassUtils.NO_ARGS);
            }
            catch (Exception e)
            {
                throw new ServiceException(CoreMessages.cannotInstanciateFinder(serviceFinderClass), e);
            }
            String realService = finder.findService(name, sd, props);
            if (realService != null)
            {
                // Recursively look up the service descriptor for the real service.
                return RegistryContext.getRegistry().lookupServiceDescriptor(ServiceDescriptorFactory.PROVIDER_SERVICE_TYPE, realService, overrides);
            }
            else 
            {
                throw new ServiceException(CoreMessages.serviceFinderCantFindService(name));
            }
        }
        return sd;
    }        
}


