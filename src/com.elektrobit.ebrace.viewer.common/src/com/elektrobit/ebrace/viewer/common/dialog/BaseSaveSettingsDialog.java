/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dialog;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;

public class BaseSaveSettingsDialog extends TitleAreaDialog
{

    private static final String HEIGHT = "height";
    private static final String WIDTH = "width";
    private static final String Y_POS = "yPos";
    private static final String X_POS = "xPos";

    public BaseSaveSettingsDialog(Shell parentShell)
    {
        super( parentShell );
        setShellStyle( getShellStyle() | SWT.RESIZE | SWT.MAX );
    }

    @Override
    protected void initializeBounds()
    {
        IDialogSettings settings = ViewerCommonPlugin.getDefault().getDialogSettings();
        // store the value of the generate sections checkbox
        String xP = settings.get( X_POS );
        String yP = settings.get( Y_POS );
        String width = settings.get( WIDTH );
        String height = settings.get( HEIGHT );
        if (xP != null && !xP.isEmpty() && yP != null && !yP.isEmpty())
        {
            int x = Integer.valueOf( xP );
            int y = Integer.valueOf( yP );
            getShell().setLocation( x, y );
        }
        if (width != null && !width.isEmpty() && height != null && !height.isEmpty())
        {
            int w = Integer.valueOf( width );
            int h = Integer.valueOf( height );
            getShell().setSize( w, h );
        }
    }

    @Override
    public boolean close()
    {
        saveBoundsSettings();
        return super.close();
    }

    protected void saveBoundsSettings()
    {
        IDialogSettings settings = ViewerCommonPlugin.getDefault().getDialogSettings();

        settings.put( X_POS, this.getShell().getLocation().x );
        settings.put( Y_POS, this.getShell().getLocation().y );
        settings.put( WIDTH, this.getShell().getSize().x );
        settings.put( HEIGHT, this.getShell().getSize().y );
    }

}
