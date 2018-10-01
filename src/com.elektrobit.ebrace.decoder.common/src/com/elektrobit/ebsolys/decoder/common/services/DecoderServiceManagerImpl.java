/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.decoder.common.services;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.decoder.common.api.DecoderServiceManager;

import lombok.extern.log4j.Log4j;

@Log4j
public final class DecoderServiceManagerImpl implements DecoderServiceManager
{
    private static DecoderServiceManager INSTANCE = new DecoderServiceManagerImpl();
    private final Map<RuntimeEventChannel<?>, DecoderService> decoderServiceCache = new HashMap<RuntimeEventChannel<?>, DecoderService>();

    private DecoderServiceManagerImpl()
    {
    }

    public static synchronized DecoderServiceManager getInstance()
    {
        return INSTANCE;
    }

    private DecoderService getDecoderServiceForEventFromOSGIRegistry(RuntimeEvent<?> event)
    {
        RuntimeEventChannel<?> channel = event.getRuntimeEventChannel();
        String channelName = channel.getName();
        DecoderService service = getServiceByChannelName( channelName );
        if (service == null)
        {
            String channelType = channel.getUnit().getDataType().getName();
            return getServiceByChannelType( channelType );
        }
        else
        {
            return service;
        }
    }

    private DecoderService getServiceByChannelName(String channelName)
    {
        String allChannelFilter = "(" + ServiceConstants.CHANNEL_NAME + "=*)";

        try
        {
            ServiceReference<?>[] channelBasedDecoderRefs = FrameworkUtil.getBundle( this.getClass() )
                    .getBundleContext().getAllServiceReferences( DecoderService.class.getName(), allChannelFilter );

            for (ServiceReference<?> references : channelBasedDecoderRefs)
            {
                Object channelOfService = references.getProperty( ServiceConstants.CHANNEL_NAME );
                if (channelOfService instanceof String)
                {
                    if (channelName.startsWith( (String)channelOfService ))
                    {
                        DecoderService foundDecoder = (DecoderService)FrameworkUtil.getBundle( this.getClass() )
                                .getBundleContext().getService( references );
                        return foundDecoder;
                    }

                }
                else if (channelOfService instanceof String[])
                {
                    String[] channelsOfService = (String[])channelOfService;
                    for (String nextChannel : channelsOfService)
                    {
                        if (channelName.startsWith( nextChannel ))
                        {
                            DecoderService foundDecoder = (DecoderService)FrameworkUtil.getBundle( this.getClass() )
                                    .getBundleContext().getService( references );
                            return foundDecoder;
                        }
                    }
                }
            }
        }
        catch (InvalidSyntaxException e)
        {
            log.warn( "Couldn't find decoder service, because filter syntax is invalid. Filter was "
                    + allChannelFilter );
        }

        return null;
    }

    private DecoderService getServiceByFilter(String filterStr)
    {
        try
        {
            ServiceReference<?>[] allServiceReferences = FrameworkUtil.getBundle( this.getClass() ).getBundleContext()
                    .getAllServiceReferences( DecoderService.class.getName(), filterStr );
            if (allServiceReferences != null && allServiceReferences.length > 0)
            {
                if (allServiceReferences.length > 1)
                {
                    log.warn( "Multiple services registered for one channel. Filter: " + filterStr );
                }
                Object service = FrameworkUtil.getBundle( this.getClass() ).getBundleContext()
                        .getService( allServiceReferences[0] );

                return (DecoderService)service;
            }
            else
            {
                return null;
            }
        }
        catch (InvalidSyntaxException e)
        {
            log.error( e.getMessage() );
            return null;
        }
    }

    private DecoderService getServiceByChannelType(String channelType)
    {
        String filterStr = "(" + ServiceConstants.CLAZZ_TYPE + "=" + channelType + ")";
        return getServiceByFilter( filterStr );
    }

    @Override
    public DecoderService getDecoderServiceForEvent(RuntimeEvent<?> event)
    {
        if (event == null)
        {
            return null;
        }

        DecoderService decoderService = decoderServiceCache.get( event.getRuntimeEventChannel() );

        if (decoderService == null)
        {
            decoderService = getDecoderServiceForEventFromOSGIRegistry( event );
            if (decoderService != null)
            {
                decoderServiceCache.put( event.getRuntimeEventChannel(), decoderService );
            }
        }

        return decoderService;
    }

    @Override
    public DecoderService getDecoderForClassName(String className)
    {
        Map<Object, Properties> listOfServices = new GenericOSGIServiceTracker<DecoderService>( DecoderService.class )
                .getServices( DecoderService.class.getName() );

        for (Object nextService : listOfServices.keySet())
        {
            if (nextService instanceof DecoderService)
            {
                Dictionary<?, ?> serviceProps = listOfServices.get( nextService );
                Object type = serviceProps.get( ServiceConstants.CLAZZ_TYPE );
                if (type instanceof String[])
                {
                    String[] types = (String[])type;
                    for (String nextType : types)
                    {
                        if (nextType.equals( className ))
                        {
                            return (DecoderService)nextService;
                        }
                    }
                }
            }
        }
        return null;
    }
}
