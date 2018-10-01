/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.common.utils;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class OSGIWhiteBoardPatternCaller<T>
{
    private final Class<T> serviceClass;
    private final BundleContext bundleContext;

    public OSGIWhiteBoardPatternCaller(Class<T> serviceClass)
    {
        this.serviceClass = serviceClass;
        this.bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
    }

    public void callOSGIService(OSGIWhiteBoardPatternCommand<T> command)
    {
        ServiceTracker<T, ServiceRegistration<T>> tracker = new ServiceTracker<T, ServiceRegistration<T>>( bundleContext,
                                                                                                           serviceClass
                                                                                                                   .getName(),
                                                                                                           null );
        tracker.open();
        try
        {
            ServiceReference<T>[] servicesReferences = tracker.getServiceReferences();
            if (servicesReferences != null)
            {
                for (ServiceReference<T> serviceReference : servicesReferences)
                {
                    T listener = FrameworkUtil.getBundle( this.getClass() ).getBundleContext()
                            .getService( serviceReference );
                    if (listener != null)
                    {
                        command.callOSGIService( listener );
                    }
                }
            }
        }
        finally
        {
            tracker.close();
        }
    }

}
