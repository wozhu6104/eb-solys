/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;

public class DemoProgressWindow extends TitleAreaDialog
{
    private static final String EXECUTING_SCRIPT_LABEL = "Executing Script      ";
    private static final String EXECUTING_SCRIPT_PROGRESS_LABEL = "Executing Script...   ";
    private static final String LOADING_FILE_LABEL = "Loading File    ";
    private static final String LOADING_FILE_PROGRESS_LABEL = "Loading File... ";
    private static final int WINDOW_NARROWER_THAN_ORIGINAL_PX = 100;
    private Font normalFont;
    private final FontData data = Display.getDefault().getSystemFont().getFontData()[0];
    private final Font boldFont = new Font( Display.getDefault(),
                                            new FontData( data.getName(), data.getHeight(), SWT.BOLD ) );

    private Image doneImage;
    private Label loadingFileLabel;
    private Label loadingFileImageLabel;
    private Label executringScriptLabel;
    private Label executringScriptImageLabel;
    private Button okButton;

    public enum STATE {
        LOADING_FILE, EXECUTING_SCRIPT, DONE
    };

    public DemoProgressWindow(Shell parentShell)
    {
        super( parentShell );
    }

    @Override
    public void create()
    {
        super.create();
        setLocationAndSize();
        adaptButtons();
        setTitle( "Demo mode will be initialized" );
        setMessage( "Please wait...", IMessageProvider.INFORMATION );
        registerDisposeListener();
    }

    private void setLocationAndSize()
    {
        Shell shell = getShell();
        Point location = shell.getLocation();
        location.y -= 220;
        location.x += WINDOW_NARROWER_THAN_ORIGINAL_PX / 2;
        shell.setLocation( location );

        Point size = shell.getSize();
        size.y -= 40;
        size.x -= WINDOW_NARROWER_THAN_ORIGINAL_PX;
        shell.setSize( size );
    }

    private void adaptButtons()
    {
        Button removedOKButton = getButton( IDialogConstants.OK_ID );
        removedOKButton.setVisible( false );

        okButton = getButton( IDialogConstants.CANCEL_ID );
        okButton.setText( "OK" );
        okButton.setEnabled( false );
    }

    private void registerDisposeListener()
    {
        okButton.addDisposeListener( new DisposeListener()
        {

            @Override
            public void widgetDisposed(DisposeEvent e)
            {
                doneImage.dispose();
            }
        } );
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        doneImage = ViewerScriptPlugin.getDefault().getImage( "complete_status", "gif" );

        Composite area = (Composite)super.createDialogArea( parent );

        Composite gridArea = new Composite( area, SWT.NONE );
        GridLayout gridLayout = new GridLayout( 2, false );
        gridLayout.horizontalSpacing = 15;
        gridLayout.marginLeft = 15;
        gridLayout.marginTop = 15;
        gridArea.setLayout( gridLayout );
        GridData gridArealayoutData = new GridData( SWT.LEFT, SWT.CENTER, true, false );
        gridArealayoutData.horizontalIndent = 0;
        gridArea.setLayoutData( gridArealayoutData );

        loadingFileLabel = new Label( gridArea, SWT.NONE );
        loadingFileLabel.setText( LOADING_FILE_LABEL );
        loadingFileImageLabel = new Label( gridArea, SWT.NONE );
        loadingFileImageLabel.setImage( doneImage );
        loadingFileImageLabel.setVisible( false );

        executringScriptLabel = new Label( gridArea, SWT.NONE );
        executringScriptLabel.setText( EXECUTING_SCRIPT_LABEL );
        executringScriptImageLabel = new Label( gridArea, SWT.NONE );
        executringScriptImageLabel.setImage( doneImage );
        executringScriptImageLabel.setVisible( false );

        return area;
    }

    public void setState(STATE state)
    {
        if (getShell() == null || getShell().isDisposed())
        {
            return;
        }

        switch (state)
        {
            case LOADING_FILE :
                setFontStyle( loadingFileLabel, SWT.BOLD );
                loadingFileLabel.setText( LOADING_FILE_PROGRESS_LABEL );
                break;
            case EXECUTING_SCRIPT :
                setFontStyle( loadingFileLabel, SWT.NORMAL );
                setFontStyle( executringScriptLabel, SWT.BOLD );
                loadingFileLabel.setText( LOADING_FILE_LABEL );
                executringScriptLabel.setText( EXECUTING_SCRIPT_PROGRESS_LABEL );
                loadingFileImageLabel.setVisible( true );
                break;
            case DONE :
                setFontStyle( executringScriptLabel, SWT.NORMAL );
                executringScriptImageLabel.setVisible( true );
                executringScriptLabel.setText( EXECUTING_SCRIPT_LABEL );
                okButton.setEnabled( true );
                setTitle( "Demo mode ready" );
                setMessage( "You can now explore EB solys", IMessageProvider.INFORMATION );
                break;
        }
    }

    private void setFontStyle(Label label, int style)
    {
        if (normalFont == null)
        {
            normalFont = label.getFont();
        }

        if (style == SWT.NORMAL)
        {
            label.setFont( normalFont );
        }

        if (style == SWT.BOLD)
        {
            label.setFont( boldFont );
        }
    }
}
