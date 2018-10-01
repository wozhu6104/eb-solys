/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.decoder.protobuf;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.ServiceConstants;
import com.elektrobit.ebrace.decoder.protobuf.services.ProtobufDecoderService;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.google.protobuf.GeneratedMessage;

public class DefaultDecoderPlugin implements BundleActivator
{

    private static BundleContext context;
    private ServiceRegistration<?> defaultProtoBufService;

    static BundleContext getContext()
    {
        return context;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        DefaultDecoderPlugin.context = bundleContext;
        startServices();
    }

    private void startServices()
    {
        defaultProtoBufService = context.registerService( DecoderService.class.getName(),
                                                          new ProtobufDecoderService(),
                                                          intializePropertiesForProtobufDecoderService() );
    }

    @SuppressWarnings("unchecked")
    private Dictionary<String, Object> intializePropertiesForProtobufDecoderService()
    {
        @SuppressWarnings("rawtypes")
        Dictionary props = new Properties();
        props.put( ServiceConstants.CLAZZ_TYPE, GeneratedMessage.class );
        return props;
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
        defaultProtoBufService.unregister();
        DefaultDecoderPlugin.context = null;
    }

}
