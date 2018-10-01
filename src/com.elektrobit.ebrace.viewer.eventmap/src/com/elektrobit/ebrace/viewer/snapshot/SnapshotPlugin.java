/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot;

import org.osgi.framework.BundleContext;

import com.elektrobit.ebrace.viewer.common.plugin.AbstractRaceUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class SnapshotPlugin extends AbstractRaceUIPlugin
{
    private static SnapshotPlugin plugin;

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

    public static AbstractRaceUIPlugin getPluginInstance()
    {
        return plugin;
    }
}
