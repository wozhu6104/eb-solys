/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin
{
    private static final String PATH_TO_ICONS = "icons" + File.separator;

    private static Activator plugin;
    private BundleContext bundleContext;

    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start( context );
        this.bundleContext = context;
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        this.bundleContext = null;
        plugin = null;
        super.stop( context );
    }

    public static Activator getDefault()
    {
        return plugin;
    }

    public BundleContext getBundleContext()
    {
        return bundleContext;
    }

    public ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin( plugin.getBundle().getSymbolicName(), path );
    }

    public Image getImage(String name, String type)
    {
        Image image = super.getImageRegistry().get( name );
        if (image == null)
        {
            ImageDescriptor descriptor = getImageDescriptor( PATH_TO_ICONS + name + "." + type );

            image = descriptor.createImage();
            super.getImageRegistry().put( name, image );
        }
        return image;
    }
}
