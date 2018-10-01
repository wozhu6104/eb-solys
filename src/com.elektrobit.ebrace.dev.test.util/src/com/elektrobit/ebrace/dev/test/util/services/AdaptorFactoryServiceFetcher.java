/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.services;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;

public class AdaptorFactoryServiceFetcher
        implements
            ServiceTrackerCustomizer<TargetAdaptorFactory, TargetAdaptorFactory>
{

    private final BundleContext bundleContext;
    private final ServiceTracker<TargetAdaptorFactory, TargetAdaptorFactory> serviceTracker;
    private final Map<String, TargetAdaptorFactory> serviceMap = new HashMap<String, TargetAdaptorFactory>();

    public AdaptorFactoryServiceFetcher()
    {
        bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
        serviceTracker = new ServiceTracker<TargetAdaptorFactory, TargetAdaptorFactory>( bundleContext,
                                                                                         TargetAdaptorFactory.class
                                                                                                 .getName(),
                                                                                         this );
        serviceTracker.open();
    }

    public boolean hasServiceForMessageType(String messageType)
    {
        return serviceMap.containsKey( messageType );
    }

    public TargetAdaptorFactory getServiceForMessageType(String messageType)
    {
        return serviceMap.get( messageType );
    }

    @Override
    public TargetAdaptorFactory addingService(ServiceReference<TargetAdaptorFactory> reference)
    {
        final TargetAdaptorFactory service = bundleContext.getService( reference );
        final String messageType = (String)reference.getProperty( "MessageType" );

        if (!serviceMap.containsKey( messageType ))
        {
            serviceMap.put( messageType, service );
        }
        else
        {
            throw new IllegalStateException( "There should always be only one Target-Adapter for one message type." );
        }

        return service;
    }

    @Override
    public void modifiedService(ServiceReference<TargetAdaptorFactory> reference, TargetAdaptorFactory service)
    {
        throw new UnsupportedOperationException( "TargetAdapter should never be modified. Target-Adapter was "
                + reference.getBundle().getSymbolicName() );
    }

    @Override
    public void removedService(ServiceReference<TargetAdaptorFactory> reference, TargetAdaptorFactory service)
    {
        final String messageType = (String)reference.getProperty( "MessageType" );
        serviceMap.remove( messageType );
    }

    public void unload()
    {
        serviceTracker.close();
        serviceMap.clear();
    }

}
