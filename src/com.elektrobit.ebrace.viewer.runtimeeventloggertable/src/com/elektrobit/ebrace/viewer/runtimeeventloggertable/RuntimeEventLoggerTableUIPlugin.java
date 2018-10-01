/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RuntimeEventLoggerTableUIPlugin extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "com.elektrobit.ebrace.viewer.runtimeeventloggertable"; //$NON-NLS-1$
    private static final String PATH_TO_ICONS = "icons" + File.separator;
    // The shared instance
    private static RuntimeEventLoggerTableUIPlugin plugin;

    /**
     * The constructor
     */
    public RuntimeEventLoggerTableUIPlugin()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop( context );
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static RuntimeEventLoggerTableUIPlugin getDefault()
    {
        return plugin;
    }

    public ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin( PLUGIN_ID, path );
    }

    public Image getImage(String name)
    {
        Image image = super.getImageRegistry().get( name );
        if (image == null)
        {
            ImageDescriptor descriptor = getImageDescriptor( PATH_TO_ICONS + name + ".png" );

            image = descriptor.createImage();
            super.getImageRegistry().put( name, image );
        }
        return image;
    }

}
