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

import java.util.Dictionary;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class GenericOSGIServiceRegistration<T>
{
    private final static Logger LOG = Logger.getLogger( GenericOSGIServiceRegistration.class );

    public static <T> ServiceRegistration<?> registerService(Class<T> serviceClass, Object serviceImplementation,
            Dictionary<String, String> properties)
    {
        return FrameworkUtil.getBundle( serviceImplementation.getClass() ).getBundleContext()
                .registerService( serviceClass.getName(), serviceImplementation, properties );
    }

    public static <T> ServiceRegistration<?> registerService(Class<T> serviceClass, Object serviceImplementation)
    {
        return FrameworkUtil.getBundle( serviceImplementation.getClass() ).getBundleContext()
                .registerService( serviceClass.getName(), serviceImplementation, null );
    }

    public static void exchangeService(Class<?> serviceClass, Object serviceImplementation, Object newServiceImpl)
    {
        ServiceReference<?> reference = FrameworkUtil.getBundle( serviceImplementation.getClass() ).getBundleContext()
                .getServiceReference( serviceClass );

        FrameworkUtil.getBundle( serviceImplementation.getClass() ).getBundleContext().ungetService( reference );

        registerService( serviceClass, newServiceImpl );
    }

    public static <T> boolean unregisterService(ServiceRegistration<T> serviceRegistration)
    {
        boolean result = false;
        if (serviceRegistration != null)
        {
            Bundle bundle = FrameworkUtil.getBundle( serviceRegistration.getClass() );
            if (bundle != null)
            {
                BundleContext context = bundle.getBundleContext();
                if (context != null)
                {
                    result = context.ungetService( serviceRegistration.getReference() );
                }
                else
                {
                    LOG.info( "Could not unregister service because bundlecontext is null. Serviceregistration was "
                            + serviceRegistration.getClass().getName() );
                }
            }
            else
            {
                LOG.info( "Could not unregister service because bundle is null. Serviceregistration was "
                        + serviceRegistration.getClass().getName() );
            }
        }
        return result;
    }
}
