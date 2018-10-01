/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.adapterregistry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DynamicTargetAdaptorResult;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdapter;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.TargetAdaptorFactory;

import lombok.extern.log4j.Log4j;

@Log4j
public class AdapterRegistry
{
    private final ServiceTracker<TargetAdaptorFactory, TargetAdaptorFactory> adapterFactoriesTracker;
    private final Map<AdapterRegistryKey, DynamicTargetAdaptorResult> adapters = new ConcurrentHashMap<AdapterRegistryKey, DynamicTargetAdaptorResult>();
    private final BundleContext bundleContext;

    public AdapterRegistry()
    {
        bundleContext = FrameworkUtil.getBundle( this.getClass() ).getBundleContext();
        adapterFactoriesTracker = new ServiceTracker<TargetAdaptorFactory, TargetAdaptorFactory>( bundleContext,
                                                                                                  TargetAdaptorFactory.class
                                                                                                          .getName(),
                                                                                                  null );
        adapterFactoriesTracker.open();
    }

    public TargetAdapter getAdapter(DataSourceContext context, MessageType msgType)
    {
        AdapterRegistryKey key = new AdapterRegistryKey( context, msgType );
        DynamicTargetAdaptorResult foundResult = adapters.get( key );
        if (foundResult != null)
        {
            return foundResult.getAdaptor();
        }
        else
        {
            DynamicTargetAdaptorResult result = createAdapter( context, msgType );
            if (result == null)
            {
                result = new DynamicTargetAdaptorResult( null, Collections.<ServiceRegistration<?>> emptyList() );
            }
            adapters.put( key, result );
            return result.getAdaptor();
        }
    }

    private DynamicTargetAdaptorResult createAdapter(DataSourceContext context, MessageType msgType)
    {
        TargetAdaptorFactory factory = getFactoryForMessageType( msgType );
        if (factory == null)
        {
            return null;
        }

        DynamicTargetAdaptorResult adaptorResult = factory.createNewInstance( context );
        return adaptorResult;
    }

    private TargetAdaptorFactory getFactoryForMessageType(MessageType msgType)
    {

        if (checkForInvalidMessage( msgType ))
        {
            return null;
        }

        ServiceReference<TargetAdaptorFactory>[] refs = adapterFactoriesTracker.getServiceReferences();
        if (refs == null)
        {
            throw new IllegalStateException( "No registered TargetAdapterFactories found" );
        }

        for (ServiceReference<TargetAdaptorFactory> sr : refs)
        {
            String messageType = (String)sr.getProperty( "MessageType" );

            if (messageType != null)
            {
                if (messageType.equals( msgType.toString() ))
                {
                    TargetAdaptorFactory factory = bundleContext.getService( sr );
                    return factory;
                }
            }
        }

        log.error( "No matching TargetAdaptorFactory found for type " + msgType );

        return null;
    }

    private boolean checkForInvalidMessage(MessageType msgType)
    {
        return (msgType.toString().equals( MessageType.MSG_TYPE_CHRONOGRAPH_CALIBRATION.toString() )
                || msgType.toString().equals( MessageType.MSG_TYPE_PROT_HNDLR_CONTROL.toString() ));
    }

    public void disposeAllAdapters()
    {
        unregisterAllOsgiInterfaces();
        callDisposeOnAdaptors();
        adapters.clear();
    }

    private void unregisterAllOsgiInterfaces()
    {
        Collection<DynamicTargetAdaptorResult> adaptorResults = adapters.values();
        for (DynamicTargetAdaptorResult adaptorResult : adaptorResults)
        {
            if (adaptorResult == null)
            {
                continue;
            }
            List<ServiceRegistration<?>> registrations = adaptorResult.getRegistrations();
            for (ServiceRegistration<?> serviceRegistration : registrations)
            {
                serviceRegistration.unregister();
            }
        }
    }

    private void callDisposeOnAdaptors()
    {
        Collection<DynamicTargetAdaptorResult> adaptorResults = adapters.values();
        for (DynamicTargetAdaptorResult adaptorResult : adaptorResults)
        {
            if (adaptorResult == null || adaptorResult.getAdaptor() == null)
            {
                continue;
            }
            TargetAdapter adaptor = adaptorResult.getAdaptor();
            adaptor.dispose();
        }
    }
}
