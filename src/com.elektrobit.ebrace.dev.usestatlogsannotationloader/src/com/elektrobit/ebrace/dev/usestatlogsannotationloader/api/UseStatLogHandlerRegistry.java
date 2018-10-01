/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.usestatlogsannotationloader.api;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class UseStatLogHandlerRegistry implements ServiceTrackerCustomizer<UseStatLogHandler, UseStatLogHandler>
{
    private final BundleContext bundleContext;
    private final List<UseStatLogHandler> services = new ArrayList<>();
    private final ServiceTracker<UseStatLogHandler, UseStatLogHandler> serviceTracker;

    public UseStatLogHandlerRegistry()
    {
        bundleContext = Platform.getBundle( "com.elektrobit.ebrace.application" ).getBundleContext();
        serviceTracker = new ServiceTracker<UseStatLogHandler, UseStatLogHandler>( bundleContext,
                                                                                   UseStatLogHandler.class.getName(),
                                                                                   this );
        serviceTracker.open();
    }

    public List<UseStatLogHandler> getUseStatLogHandler()
    {
        return services;
    }

    @Override
    public UseStatLogHandler addingService(ServiceReference<UseStatLogHandler> reference)
    {
        UseStatLogHandler service = bundleContext.getService( reference );
        services.add( service );
        return service;
    }

    @Override
    public void modifiedService(ServiceReference<UseStatLogHandler> reference, UseStatLogHandler service)
    {
        services.remove( service );
        services.add( service );
    }

    @Override
    public void removedService(ServiceReference<UseStatLogHandler> reference, UseStatLogHandler service)
    {
        services.remove( service );
    }

    @Override
    protected void finalize() throws Throwable
    {
        serviceTracker.close();
        super.finalize();
    }

}
