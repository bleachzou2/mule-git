/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.impl;

import org.mule.RegistryContext;
import org.mule.config.MuleConfiguration;
import org.mule.config.QueueProfile;
import org.mule.config.ThreadingProfile;
import org.mule.routing.inbound.InboundPassThroughRouter;
import org.mule.routing.inbound.InboundRouterCollection;
import org.mule.routing.nested.NestedRouterCollection;
import org.mule.routing.outbound.OutboundPassThroughRouter;
import org.mule.routing.outbound.OutboundRouterCollection;
import org.mule.routing.response.ResponseRouterCollection;
import org.mule.umo.UMOImmutableDescriptor;
import org.mule.umo.UMOManagementContext;
import org.mule.umo.lifecycle.InitialisationException;
import org.mule.umo.model.UMOEntryPointResolverSet;
import org.mule.umo.routing.UMOInboundRouterCollection;
import org.mule.umo.routing.UMONestedRouterCollection;
import org.mule.umo.routing.UMOOutboundRouterCollection;
import org.mule.umo.routing.UMOResponseRouterCollection;

import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <code>MuleDescriptor</code> describes all the properties for a Mule UMO. New
 * Mule UMOs can be initialised as needed from their descriptor.
 */

public class ImmutableMuleDescriptor implements UMOImmutableDescriptor
{
    /** The initial states that the component can be started in */
    public static final String INITIAL_STATE_STOPPED = "stopped";
    public static final String INITIAL_STATE_STARTED = "started";
    public static final String INITIAL_STATE_PAUSED = "paused";

    /** holds the exception stategy for this UMO */
    protected ExceptionListener exceptionListener;

    /** Factory which creates an instance of the actual service object. */
    //protected ObjectFactory serviceFactory;

    /** The descriptor name */
    protected String name;

    /**
     * The properties for the Mule UMO.
     *
     * @deprecated MULE-1933 Properties for the underlying service should be set on the ServiceFactory instead.
     */
    protected Map properties = new HashMap();

    protected UMOInboundRouterCollection inboundRouter;

    protected UMOOutboundRouterCollection outboundRouter;

    protected UMONestedRouterCollection nestedRouter;

    protected UMOResponseRouterCollection responseRouter;

    /**
     * The threading profile to use for this component. If this is not set a default
     * will be provided by the server
     */
    protected ThreadingProfile threadingProfile;

    /**
     * The queue profile to use for this component. If this is not set a default
     * will be provided by the server
     */
    protected QueueProfile queueProfile;

    /**
     * Determines the initial state of this component when the model starts. Can be
     * 'stopped' or 'started' (default)
     */
    protected String initialState = INITIAL_STATE_STARTED;

    protected List initialisationCallbacks = new ArrayList();

    /** The name of the model that this descriptor is associated with */
    protected String modelName;

    protected UMOManagementContext managementContext;

    protected UMOEntryPointResolverSet entryPointResolverSet;

    /**
     * Default constructor. Initalises common properties for the MuleConfiguration
     * object
     *
     * @see org.mule.config.MuleConfiguration
     */
    public ImmutableMuleDescriptor(ImmutableMuleDescriptor descriptor)
    {
        inboundRouter = descriptor.getInboundRouter();
        outboundRouter = descriptor.getOutboundRouter();
        responseRouter = descriptor.getResponseRouter();
        nestedRouter = descriptor.getNestedRouter();
        //serviceFactory = descriptor.getServiceFactory();
        properties = descriptor.getProperties();
        name = descriptor.getName();

        threadingProfile = descriptor.getThreadingProfile();
        exceptionListener = descriptor.getExceptionListener();
        initialState = descriptor.getInitialState();
        modelName = descriptor.getModelName();
        entryPointResolverSet = descriptor.getEntryPointResolverSet();
    }

    /**
     * Default constructor used by mutable versions of this class to provide defaults
     * for certain properties
     */
    protected ImmutableMuleDescriptor()
    {
        inboundRouter = new InboundRouterCollection();
        inboundRouter.addRouter(new InboundPassThroughRouter());

        outboundRouter = new OutboundRouterCollection();
        responseRouter = new ResponseRouterCollection();
        nestedRouter = new NestedRouterCollection();

    }

    public void initialise() throws InitialisationException
    {
        MuleConfiguration config = RegistryContext.getConfiguration();
        if (threadingProfile == null)
        {
            threadingProfile = config.getDefaultComponentThreadingProfile();
        }

        if (inboundRouter == null)
        {
            // Create Default routes that route to the default inbound and
            // outbound endpoints
            inboundRouter = new InboundRouterCollection();
            inboundRouter.addRouter(new InboundPassThroughRouter());
        }

        if (outboundRouter == null)
        {
            outboundRouter = new OutboundRouterCollection();
            outboundRouter.addRouter(new OutboundPassThroughRouter());
        }
//        inboundRouter.initialise();
//        outboundRouter.initialise();
//        if(responseRouter !=null)
//        {
//            responseRouter.initialise();
//        }
//        if(nestedRouter !=null)
//        {
//            nestedRouter.initialise();
//        }

//        if (serviceFactory != null)
//        {
//            serviceFactory.initialise();
//        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.mule.umo.UMODescriptor#getExceptionListener()
     */
    public ExceptionListener getExceptionListener()
    {
        return exceptionListener;
    }


    /*
     * (non-Javadoc)
     *
     * @see org.mule.umo.UMODescriptor#getName()
     */
    public String getName()
    {
        return name;
    }

    /*
     * @see org.mule.umo.UMODescriptor#getParams() Not HashMap is used instead of Map
     *      due to a Spring quirk where the property is not found if specified as a
     *      map
     *      
     * @deprecated MULE-1933 Properties for the underlying service should be set on the ServiceFactory instead.
     */
    public Map getProperties()
    {
        return properties;
    }

    /** Factory which creates an instance of the actual service object. */
//    public ObjectFactory getServiceFactory()
//    {
//        return serviceFactory;
//    }

    public UMOInboundRouterCollection getInboundRouter()
    {
        return inboundRouter;
    }

    public UMOOutboundRouterCollection getOutboundRouter()
    {
        return outboundRouter;
    }

    public UMONestedRouterCollection getNestedRouter()
    {
        return nestedRouter;
    }

    /**
     * The threading profile used by the UMO when managing a component. Can be used
     * to allocate more or less resources to this particular umo component.
     */
    public ThreadingProfile getThreadingProfile()
    {
        return threadingProfile;
    }

    public QueueProfile getQueueProfile()
    {
        return queueProfile;
    }

    public void fireInitialisationCallbacks(Object component) throws InitialisationException
    {
        InitialisationCallback callback;
        for (Iterator iterator = initialisationCallbacks.iterator(); iterator.hasNext();)
        {
            callback = (InitialisationCallback) iterator.next();
            callback.initialise(component);
        }
    }

    public UMOResponseRouterCollection getResponseRouter()
    {
        return responseRouter;
    }

    public String getInitialState()
    {
        return initialState;
    }

    public UMOManagementContext getManagementContext()
    {
        return managementContext;
    }

    public String getModelName()
    {
        return modelName;
    }

    /**
     * A descriptor can have a custom entrypoint resolver for its own object.
     * By default this is null. When set this resolver will override the resolver on the model
     *
     * @return Null is a resolver set has not been set otherwise the resolver to use
     *         on this component
     */
    public UMOEntryPointResolverSet getEntryPointResolverSet()
    {
        return entryPointResolverSet;
    }

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("ImmutableMuleDescriptor");
        sb.append("{exceptionListener=").append(exceptionListener);
        //sb.append(", serviceFactory=").append(serviceFactory);
        sb.append(", name='").append(name).append('\'');
        sb.append(", properties=").append(properties);
        sb.append(", threadingProfile=").append(threadingProfile);
        sb.append(", initialState='").append(initialState).append('\'');
        sb.append(", modelName='").append(modelName).append('\'');
        sb.append(", entryPointResolver='").append(entryPointResolverSet).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
