/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CenteredColorDialog
{
    private final Shell m_parentShell;
    private Shell m_centerShell;
    private ColorDialog m_colorDialog;

    public CenteredColorDialog(Shell parentShell)
    {
        this.m_parentShell = parentShell;
        init();
    }

    private void init()
    {
        m_centerShell = new Shell( m_parentShell, SWT.NO_TRIM );
        m_centerShell.setBackground( new Color( Display.getCurrent(), 255, 0, 0 ) );

        // With this we can basically set the location of the ColorDialog
        // centering, it will require a little bit of guessing since the width and
        // height of the dialog are unknown.
        int guessColorDialogWidth = 222;
        int guessColorDialogHeight = 306;

        int colorDialogPosX = computeDialogXPos( guessColorDialogWidth );
        int colorDialogPosY = computeDialogYPos( guessColorDialogHeight );

        m_centerShell.setSize( 0, 0 ); // make it invisible
        m_centerShell.setLocation( colorDialogPosX, colorDialogPosY );
        m_colorDialog = new ColorDialog( m_centerShell, SWT.NONE );
    }

    private int computeDialogXPos(int guessColorDialogWidth)
    {
        int parentShellX1 = m_parentShell.getBounds().x;
        int parentShellX2 = m_parentShell.getBounds().x + m_parentShell.getBounds().width;
        int centeredPosXParentShell = (parentShellX1 + parentShellX2) / 2;
        int colorDialogPosX = centeredPosXParentShell - guessColorDialogWidth / 2;
        return colorDialogPosX;
    }

    private int computeDialogYPos(int guessColorDialogHeight)
    {
        int parentShellY1 = m_parentShell.getBounds().y;
        int parentShellY2 = m_parentShell.getBounds().y + m_parentShell.getBounds().height;
        int centeredPosYParentShell = (parentShellY1 + parentShellY2) / 2;
        int colorDialogPosY = centeredPosYParentShell - guessColorDialogHeight / 2;
        return colorDialogPosY;
    }

    public RGB open()
    {

        m_centerShell.open();
        RGB colorChose = m_colorDialog.open();
        m_centerShell.dispose();

        return colorChose;
    }

    public void setRGB(RGB rgb)
    {
        m_colorDialog.setRGB( rgb );
    }

    public void setText(String text)
    {
        m_colorDialog.setText( text );
    }
}
