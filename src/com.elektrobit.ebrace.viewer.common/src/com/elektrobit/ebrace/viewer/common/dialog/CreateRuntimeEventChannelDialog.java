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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class CreateRuntimeEventChannelDialog extends BaseSaveSettingsDialog
{

    private static final String DEFAULT_SHELL_TEXT = "Create channel";
    private static final String DEFAULT_TEXT = "Please enter a name and select a channel type.";
    private static final String TITLE = "Create new runtime event channel";
    private static final String ERROR_TEXT_CHANNEL_WITH_NAME_EXISTS = "A channel with the given name already exists.";
    private static final String ERROR_TEXT_CHANNEL_EMPTY_NAME = "Please enter a valid name.";
    private Text channelNameTextFiled;
    private Text channelDescriptionFiled;
    private Combo channelTypeDropdown;
    String channelName;
    Class<?> channelType;
    String description;
    List<String> channelnames;

    public CreateRuntimeEventChannelDialog(Shell parentShell)
    {
        super( parentShell );
    }

    private void initializeExistingChannelNames()
    {
        channelnames = new ArrayList<String>();
        RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
        for (RuntimeEventChannel<?> channel : runtimeEventAcceptor.getRuntimeEventChannels())
        {
            String name = channel.getName();
            if (!channelnames.contains( name ))
                channelnames.add( name );
        }
    }

    @Override
    protected Point getInitialSize()
    {
        return new Point( 400, 300 );
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite container = new Composite( parent, SWT.BORDER );

        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        super.createContents( container );
        Composite content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout( 2, false ) );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        createNamePart( content );
        createChannelTypePart( content );
        createDescrPart( content );
        registerListeners();
        channelTypeDropdown.select( 0 );
        getButton( IDialogConstants.OK_ID ).setEnabled( false );
        setTitle( TITLE );
        setMessage( DEFAULT_TEXT );
        getShell().setText( DEFAULT_SHELL_TEXT );
        initializeExistingChannelNames();
        return container;
    }

    private void createNamePart(Composite parent)
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( "Name: " );
        channelNameTextFiled = new Text( parent, SWT.BORDER );
        channelNameTextFiled.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    }

    private void createDescrPart(Composite parent)
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( "Description: " );
        channelDescriptionFiled = new Text( parent, SWT.BORDER );
        channelDescriptionFiled.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }

    private void createChannelTypePart(Composite parent)
    {
        Label label = new Label( parent, SWT.NONE );
        label.setText( "Type: " );
        channelTypeDropdown = new Combo( parent, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY );
        channelTypeDropdown.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
        initializeChanneltypes();
    }

    private void initializeChanneltypes()
    {
        this.channelTypeDropdown.add( Boolean.class.getSimpleName() );
        this.channelTypeDropdown.add( String.class.getSimpleName() );
        this.channelTypeDropdown.add( Integer.class.getSimpleName() );
        this.channelTypeDropdown.add( Long.class.getSimpleName() );
        this.channelTypeDropdown.add( Double.class.getSimpleName() );
    }

    private void registerListeners()
    {
        this.channelNameTextFiled.addModifyListener( new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                boolean okButtonEnabled = true;
                setErrorMessage( null );
                if (channelNameTextFiled.getText().trim().isEmpty())
                {
                    okButtonEnabled = false;
                    setErrorMessage( ERROR_TEXT_CHANNEL_EMPTY_NAME );
                }
                else if (channelnames.contains( channelNameTextFiled.getText() ))
                {
                    okButtonEnabled = false;
                    setErrorMessage( ERROR_TEXT_CHANNEL_WITH_NAME_EXISTS );
                }

                getButton( IDialogConstants.OK_ID ).setEnabled( okButtonEnabled );
            }
        } );
    }

    @Override
    protected void okPressed()
    {
        this.channelName = channelNameTextFiled.getText();
        this.description = this.channelDescriptionFiled.getText();
        try
        {
            channelType = Class.forName( "java.lang." + channelTypeDropdown.getText() );
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
        super.okPressed();
    }

    public String getDescription()
    {
        return description;
    }

    public Class<?> getChannelType()
    {
        return this.channelType;
    }

    public String getChannelName()
    {
        return channelName;
    }

}
