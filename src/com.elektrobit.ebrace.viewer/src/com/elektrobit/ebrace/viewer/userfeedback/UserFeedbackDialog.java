/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.userfeedback;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UserFeedbackDialog extends Dialog
{
    private Composite container;
    private Combo subjectCombo;
    private Text textName;
    private Text textComment;
    private Button sendButton;

    private String feedbackName;
    private String feedbackComment;
    private String feedbackSubject;
    private byte sendCheckLogic;
    private final Shell parentShell;

    private static final String EMAIL = "mailto:ebsolys-key@elektrobit.com?subject=user feedback&body=";

    private final Desktop desktop = Desktop.getDesktop();

    public UserFeedbackDialog(Shell parentShell)
    {
        super( parentShell );
        this.parentShell = parentShell;
        sendCheckLogic = 0;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite area = (Composite)super.createDialogArea( parent );
        container = new Composite( area, SWT.NONE );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        GridLayout layout = new GridLayout( 2, false );
        container.setLayout( layout );
        layout.verticalSpacing = 15;

        createSubject( container );
        createName( container );
        createComment( container );

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent)
    {
        super.createButtonsForButtonBar( parent );

        sendButton = getButton( IDialogConstants.OK_ID );
        sendButton.setText( "Send" );
        setButtonLayoutData( sendButton );
        sendButton.setEnabled( false );

    }

    private void createSubject(Composite container)
    {

        String items[] = {"Issue Report", "Stack Trace", "Proposal for Improvement", "Feature Request", "Other"};

        Label lbt = new Label( container, SWT.NONE );
        lbt.setText( "Subject:  " );

        GridData gridSubject = new GridData();
        gridSubject.grabExcessHorizontalSpace = true;
        gridSubject.horizontalAlignment = GridData.FILL;

        subjectCombo = new Combo( container, SWT.READ_ONLY );
        subjectCombo.setItems( items );
        subjectCombo.select( 4 );
        subjectCombo.setLayoutData( gridSubject );
    }

    private void createName(Composite container)
    {

        Label lbt = new Label( container, SWT.NONE );
        lbt.setText( "Name:  " );

        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        textName = new Text( container, SWT.BORDER );
        textName.setLayoutData( dataFirstName );

        textName.addModifyListener( new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                setSendLogicValue( textName, (byte)2, (byte)1 );

            }
        } );
    }

    private void createComment(Composite container)
    {
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.BEGINNING;
        Label lbt = new Label( container, SWT.NONE );
        lbt.setText( "Comments:  " );
        lbt.setLayoutData( gridData );

        GridData gridComment = new GridData();
        gridComment.verticalAlignment = GridData.FILL_BOTH;
        gridComment.horizontalAlignment = GridData.FILL;
        gridComment.heightHint = 250;

        textComment = new Text( container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );
        textComment.setLayoutData( gridComment );

        textComment.addModifyListener( new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                setSendLogicValue( textComment, (byte)1, (byte)2 );
            }
        } );
    }

    @Override
    protected void okPressed()
    {
        saveInput();
        openEmailClientAWT();
        super.okPressed();
    }

    private void saveInput()
    {
        feedbackComment = textComment.getText();
        feedbackName = textName.getText();
        feedbackSubject = subjectCombo.getItem( subjectCombo.getSelectionIndex() ).toString();
    }

    private void openEmailClientAWT()
    {

        URI uri = null;
        try
        {
            uri = URIUtil.fromString( EMAIL + getBodyEmail() );
        }
        catch (URISyntaxException e)
        {
            MessageBox box = new MessageBox( parentShell, SWT.ICON_ERROR | SWT.OK );
            box.setMessage( "Please try again" );
            box.setText( "Unable to send your message" );
            box.open();
        }

        try
        {
            desktop.mail( uri );

        }
        catch (IOException e1)
        {
            MessageBox box = new MessageBox( parentShell, SWT.ICON_ERROR | SWT.OK );
            box.setMessage( "There is not an Email-Client available" );
            box.setText( "Unable to send your message" );
            box.open();
        }
    }

    private String getBodyEmail()
    {

        String bodyEmail = "Hello EB solys team,\n\nI would like to send you my feedback\n\n" + "Subject: "
                + feedbackSubject + "\n\n" + "Name: " + feedbackName + "\n\n" + "Comments: " + feedbackComment;

        return bodyEmail;
    }

    private void setSendLogicValue(Text text, byte full, byte empty)
    {
        if (text.getText().length() >= 1)
        {
            sendCheckLogic |= full;
        }
        else
        {
            sendCheckLogic &= empty;
        }
        buttonEnableCheck( sendCheckLogic );
    }

    private void buttonEnableCheck(byte input)
    {
        if ((input & 3) == 3)
        {
            sendButton.setEnabled( true );
        }
        else
        {
            sendButton.setEnabled( false );
        }

    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell( newShell );
        newShell.setText( "Help us improving our product with valuable feedback" );
    }

    @Override
    protected Point getInitialSize()
    {
        return new Point( 450, 450 );
    }

    @Override
    protected boolean isResizable()
    {
        return false;
    }
}
