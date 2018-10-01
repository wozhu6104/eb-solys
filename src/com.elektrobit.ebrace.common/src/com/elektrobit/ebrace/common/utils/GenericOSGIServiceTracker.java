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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class GenericOSGIServiceTracker<T> implements ServiceTrackerCustomizer<T, T>
{
    private T service;
    private T defaultServiceImplementation;
    private BundleContext bundleContext;

    public GenericOSGIServiceTracker(Class<T> serviceClass)
    {
        bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
        ServiceTracker<T, T> serviceTracker = new ServiceTracker<T, T>( bundleContext, serviceClass.getName(), this );
        serviceTracker.open();
    }

    public GenericOSGIServiceTracker(Class<T> serviceClass, T defaultImplementation)
    {
        this( serviceClass );
        this.defaultServiceImplementation = defaultImplementation;
    }

    public T getService()
    {
        if (service == null)
        {
            if (defaultServiceImplementation == null)
            {
                return null;
            }
            return defaultServiceImplementation;
        }
        return service;
    }

    public Map<Object, Properties> getServices(String clazz)
    {
        Map<Object, Properties> services = new HashMap<Object, Properties>();
        try
        {
            final ServiceReference<?>[] refs = bundleContext.getAllServiceReferences( clazz, null );
            if (refs != null)
            {
                for (ServiceReference<?> sr : refs)
                {
                    services.put( bundleContext.getService( sr ), getPropertiesForService( sr ) );
                }
            }
        }
        catch (InvalidSyntaxException e)
        {
            e.printStackTrace();
        }
        return services;
    }

    private Properties getPropertiesForService(ServiceReference<?> serviceReference)
    {
        String[] propertyKeys = serviceReference.getPropertyKeys();
        Properties result = new Properties();
        for (String propertyKey : propertyKeys)
        {
            result.put( propertyKey, serviceReference.getProperty( propertyKey ) );
        }
        return result;
    }

    @Override
    public T addingService(ServiceReference<T> reference)
    {
        T service = bundleContext.getService( reference );
        this.setService( service );
        return service;
    }

    @Override
    public void modifiedService(ServiceReference<T> reference, T service)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removedService(ServiceReference<T> reference, T service)
    {
        this.setService( null );
    }

    private void setService(T service)
    {
        this.service = service;
    }

}
