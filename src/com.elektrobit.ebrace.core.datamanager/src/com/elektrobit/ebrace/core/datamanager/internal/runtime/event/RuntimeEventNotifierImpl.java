/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.elektrobit.ebrace.core.datamanager.PluginConstants;
import com.elektrobit.ebrace.core.datamanager.internal.runtime.event.api.RuntimeEventNotifier;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventLoggerClient;

public class RuntimeEventNotifierImpl
        implements
            RuntimeEventNotifier,
            ServiceTrackerCustomizer<RuntimeEventLoggerClient, RuntimeEventLoggerClient>
{
    private final Set<RuntimeEventLoggerClient> clients = new CopyOnWriteArraySet<RuntimeEventLoggerClient>();
    private final BundleContext bundleContext;
    private final ServiceTracker<RuntimeEventLoggerClient, RuntimeEventLoggerClient> serviceTracker;

    public RuntimeEventNotifierImpl()
    {
        bundleContext = Platform.getBundle( PluginConstants.PLUGIN_ID ).getBundleContext();
        serviceTracker = new ServiceTracker<RuntimeEventLoggerClient, RuntimeEventLoggerClient>( bundleContext,
                                                                                                 RuntimeEventLoggerClient.class,
                                                                                                 this );
        serviceTracker.open();
    }

    @Override
    public void notifyAboutNewEvent(RuntimeEvent<?> newEvent)
    {
        for (RuntimeEventLoggerClient nextClient : clients)
        {
            nextClient.runtimeEventOccured( newEvent );
        }
    }

    @Override
    public RuntimeEventLoggerClient addingService(ServiceReference<RuntimeEventLoggerClient> reference)
    {
        RuntimeEventLoggerClient service = bundleContext.getService( reference );
        clients.add( service );
        return service;
    }

    @Override
    public void modifiedService(ServiceReference<RuntimeEventLoggerClient> reference, RuntimeEventLoggerClient service)
    {
    }

    @Override
    public void removedService(ServiceReference<RuntimeEventLoggerClient> reference, RuntimeEventLoggerClient service)
    {
        clients.remove( service );
    }

    public void dispose()
    {
        serviceTracker.close();
    }

}
