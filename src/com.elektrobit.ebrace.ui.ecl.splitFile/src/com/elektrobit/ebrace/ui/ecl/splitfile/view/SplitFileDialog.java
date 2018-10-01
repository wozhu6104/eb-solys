/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.splitfile.view;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.splitfile.SplitFileInteractionUseCase;

public class SplitFileDialog extends TitleAreaDialog implements SplitFileInteractionCallback
{
    private static final String START_BUTTON_LABEL = "Start";
    private static final String DIALOG_TITLE = "Splitting file";
    private static final String DIALOG_MESSAGE = "The splitted files are saved in the following folder";
    private static final String LABEL_INITIAL_STATUS = "Status: READY";
    private static final String LABEL_DONE_STATUS = "Status: DONE";
    private static final String LABEL_SPLITTING_STARTED_STATUS = "Status: RUNNING";
    private static final String LABEL_ERROR_STATUS = "An error has occurred during file splitting";

    private final String path;
    private Label labelStatus;
    private Button buttonStart;

    private SplitFileInteractionUseCase splitFileInteractionUseCase;

    public SplitFileDialog(Shell parentShell, String path)
    {
        super( parentShell );
        this.path = path;
    }

    @Override
    public void create()
    {
        super.create();
        setTitle( DIALOG_TITLE );
        setMessage( DIALOG_MESSAGE, IMessageProvider.INFORMATION );
        changeOkAndCancelButtons();
        registerDisposeListener();
        setSizeForModal();
        setLocation( getShell() );
        registerForUseCases();
    }

    private void registerForUseCases()
    {
        splitFileInteractionUseCase = UseCaseFactoryInstance.get().makeSplitFileInteractionUseCase( this );
    }

    private void setLocation(Shell shell)
    {
        Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
        Rectangle rect = shell.getBounds();
        shell.setLocation( bounds.x + (bounds.width - rect.width) / 2, bounds.y + (bounds.height - rect.height) / 2 );
    }

    @Override
    public int open()
    {
        boolean okPressed = MessageDialog
                .openQuestion( null,
                               "File is too big",
                               "File is too big to be loaded at once. Do you want to split it? "
                                       + "\n\nHint: You can process file of any size if you start a callback script before loading the file "
                                       + "(data from beginning of the file may not be available when loading is finished)." );
        if (okPressed)
        {
            return super.open();
        }
        else
        {
            return OK;
        }
    }

    private void changeOkAndCancelButtons()
    {
        Button buttonOk = getButton( IDialogConstants.OK_ID );
        buttonOk.setVisible( false );
        buttonStart = getButton( IDialogConstants.CANCEL_ID );
        buttonStart.setText( START_BUTTON_LABEL );
    }

    private void registerDisposeListener()
    {
        buttonStart.addDisposeListener( (e) -> dispose() );
    }

    private void setSizeForModal()
    {
        getShell().setSize( 457, 250 );
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite area = (Composite)super.createDialogArea( parent );
        createTextForPath( area );
        createLabel( area );
        return area;
    }

    private void dispose()
    {
        splitFileInteractionUseCase.unregister();
    }

    @Override
    protected void cancelPressed()
    {
        // Cancel button has been renamed to start
        onStartPressed();
    };

    private void onStartPressed()
    {
        splitFileInteractionUseCase.startSplitting( path );
    }

    private void createTextForPath(Composite area)
    {
        Composite container = new Composite( area, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        layout.marginTop = 10;
        layout.horizontalSpacing = 15;
        layout.marginLeft = 10;
        container.setLayout( layout );
        StyledText text = new StyledText( container, SWT.WRAP | SWT.V_SCROLL );
        text.setAlwaysShowScrollBars( false );
        text.setBackground( area.getBackground() );

        GridData gridData = new GridData( 410, 60 );
        text.setLayoutData( gridData );
        text.setEditable( false );

        File file = new File( path );
        String parent = file.getParent();
        text.setText( parent );
    }

    private void createLabel(Composite areaComposite)
    {
        GridLayout gridLayout = new GridLayout( 1, false );
        gridLayout.marginTop = 35;
        Composite labelComposite = new Composite( areaComposite, SWT.NULL );
        labelComposite.setLayout( gridLayout );
        GridData gridData = new GridData( 300, 50 );
        gridData.verticalAlignment = SWT.TOP;

        labelStatus = new Label( labelComposite, SWT.NONE );
        FontData fontData = labelStatus.getFont().getFontData()[0];
        Font font = new Font( Display.getDefault(),
                              new FontData( fontData.getName(), fontData.getHeight(), SWT.BOLD ) );
        labelStatus.setFont( font );
        labelStatus.setLayoutData( gridData );
        labelStatus.setText( LABEL_INITIAL_STATUS );
    }

    @Override
    public void onFileSplittingStarted()
    {
        buttonStart.setEnabled( false );
        if (!labelStatus.isDisposed())
        {
            labelStatus.setText( LABEL_SPLITTING_STARTED_STATUS );
        }
    }

    @Override
    public void onFileSplittingDone()
    {
        if (!labelStatus.isDisposed())
        {
            labelStatus.setText( LABEL_DONE_STATUS );
        }
        super.cancelPressed();
    }

    @Override
    public void onFileSplittingError(String message)
    {
        if (!labelStatus.isDisposed())
        {
            labelStatus.setText( LABEL_ERROR_STATUS );
            MessageDialog.openError( getShell(), LABEL_ERROR_STATUS, message );
        }
    }
}
