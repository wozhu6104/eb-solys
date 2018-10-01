/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.plugin;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public abstract class AbstractRaceUIPlugin extends AbstractUIPlugin
{
    public static final String PATH_TO_ICONS = "icons" + File.separator;

    public ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin( getBundle().getSymbolicName(), path );
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
