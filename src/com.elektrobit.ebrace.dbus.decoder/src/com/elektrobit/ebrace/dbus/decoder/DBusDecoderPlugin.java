/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dbus.decoder;

import java.util.Dictionary;
import java.util.Properties;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.dbus.decoder.services.DBusUmlDecoderService;
import com.elektrobit.ebsolys.decoder.common.api.UmlServiceConstants;
import com.elektrobit.ebsolys.decoder.common.services.UmlDecoderService;

public class DBusDecoderPlugin extends Plugin
{
    private static DBusDecoderPlugin plugin;
    private ServiceRegistration<?> dbusUmlDecoderService;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        plugin = this;
        startServices();
    }

    @SuppressWarnings("unchecked")
    private void startServices()
    {
        @SuppressWarnings("rawtypes")
        Dictionary umlProps = new Properties();
        umlProps.put( UmlServiceConstants.TREE_LAYER_TYPE, UmlServiceConstants.DBUS_TREE_LAYER_TYPE );
        dbusUmlDecoderService = plugin.getBundle().getBundleContext()
                .registerService( UmlDecoderService.class.getName(), new DBusUmlDecoderService(), umlProps );
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        stopServices();
        plugin = null;
        super.stop( context );
    }

    private void stopServices()
    {
        dbusUmlDecoderService.unregister();
    }

    public static DBusDecoderPlugin getDefault()
    {
        return plugin;
    }
}
