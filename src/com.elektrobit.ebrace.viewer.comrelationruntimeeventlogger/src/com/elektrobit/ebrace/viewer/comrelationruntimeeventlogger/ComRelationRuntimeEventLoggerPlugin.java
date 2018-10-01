/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.comrelationruntimeeventlogger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ComRelationRuntimeEventLoggerPlugin extends AbstractUIPlugin
{
    private static ComRelationRuntimeEventLoggerPlugin plugin;

    public ComRelationRuntimeEventLoggerPlugin()
    {
    }

    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

    public static ComRelationRuntimeEventLoggerPlugin getDefault()
    {
        return plugin;
    }

}
