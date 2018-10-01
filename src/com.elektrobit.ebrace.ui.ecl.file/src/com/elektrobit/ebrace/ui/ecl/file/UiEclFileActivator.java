/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.file;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class UiEclFileActivator extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.ui.ecl.file";
    private static UiEclFileActivator plugin;

    public UiEclFileActivator()
    {
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

    public static UiEclFileActivator getDefault()
    {
        return plugin;
    }

}
