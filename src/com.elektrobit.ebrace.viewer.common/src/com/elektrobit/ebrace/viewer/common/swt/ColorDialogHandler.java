/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class ColorDialogHandler
{

    private final ResourceManager resourceManager;

    public ColorDialogHandler(ResourceManager resManager)
    {
        this.resourceManager = resManager;
    }

    public Color getSelectedColor()
    {
        Shell shOrigin = Display.getCurrent().getActiveShell();
        Shell shChild = new Shell( shOrigin );
        Color choosedColor = null;
        setShellLocation( shOrigin, shChild );
        ColorDialog colorDialog = new ColorDialog( shChild );
        RGB rgb = colorDialog.open();

        if (rgb != null)
        {
            choosedColor = resourceManager.createColor( rgb );
            if (choosedColor != null)
            {
                return choosedColor;
            }
        }

        return choosedColor;
    }

    private void setShellLocation(Shell shellOrigin, Shell shellChild)
    {
        Monitor primary = shellOrigin.getMonitor();
        Rectangle bounds = primary.getBounds();

        int x = bounds.x + (bounds.width) / 2;
        int y = bounds.y + (bounds.height) / 2;

        shellChild.setLocation( x, y );
    }

}
