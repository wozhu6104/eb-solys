/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.splashHandlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * Displays license expiration date on the splash screen when RACE starts
 */
public class ExtensibleSplashHandler extends AbstractSplashHandler
{
    private Composite textPanel = null;

    private String getSplashText()
    {
        return "";
    }

    @Override
    public void init(final Shell splash)
    {
        super.init( splash );

        configureSplashLayout();
        createUI();
        splash.layout( true );

        doEventLoop();
    }

    private void doEventLoop()
    {
        // pause for 2 seconds so the text displays
        try
        {
            Display.getCurrent().update();
            Thread.sleep( 2000 );
        }
        catch (InterruptedException e)
        {
        }
    }

    private void createUI()
    {
        Shell splash = getSplash();
        splash.setBackgroundMode( SWT.INHERIT_DEFAULT );

        textPanel = new Composite( splash, SWT.NONE );
        textPanel.setLayoutData( new GridData( SWT.CENTER, SWT.END, true, true ) );
        GridLayout gridLayout = new GridLayout( 1, true );
        gridLayout.marginBottom = 3;
        textPanel.setLayout( gridLayout );

        Label label = new Label( textPanel, SWT.NONE );
        label.setText( getSplashText() );
        Display d = label.getDisplay();
        Color textColor = d.getSystemColor( SWT.COLOR_WHITE );
        label.setForeground( textColor );
        label.setLayoutData( new GridData( SWT.CENTER, SWT.END, true, true ) );
        FontData fontData = label.getFont().getFontData()[0];
        fontData.setStyle( SWT.BOLD );
        fontData.setHeight( 10 );
        label.setFont( new Font( Display.getCurrent(), fontData ) );
    }

    private void configureSplashLayout()
    {
        GridLayout layout = new GridLayout( 1, true );
        layout.marginBottom = 0;
        getSplash().setLayout( layout );
    }

    @Override
    public void dispose()
    {
        textPanel.dispose();
        super.dispose();
    }

}
