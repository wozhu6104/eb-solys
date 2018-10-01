/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.targetconnector;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebrace.viewer.common.view.SolysButton;
import com.elektrobit.ebrace.viewer.resources.connection.EditHandler;
import com.elektrobit.ebrace.viewer.script.api.StartDemoHandler;

public class ConnectToTargetModalWindow extends TitleAreaDialog implements FileSizeLimitNotifyCallback
{
    private static final int WINDOW_WIDTH = 400;
    private static final int CONNECT_BUTTON_ID = IDialogConstants.CLIENT_ID + 0;
    private static final int OPEN_FILE_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;
    private static final int EDIT_CONNECTION_BUTTON_ID = IDialogConstants.CLIENT_ID + 2;
    private static final int START_DEMO_BUTTON_ID = IDialogConstants.CLIENT_ID + 3;
    private static final int UPGRADE_BUTTON_ID = IDialogConstants.CLIENT_ID + 4;

    private static final String CONNECTING_MESSAGE_TEXT = "Connecting...";
    private static final String OPEN_FILE_BUTTON_LABEL = " Open File";
    private static final String OPEN_FILE_BUTTON_LABEL_WITH_LIMIT_PART1 = OPEN_FILE_BUTTON_LABEL + " (max ";
    private static final String OPEN_FILE_BUTTON_LABEL_WITH_LIMIT_PART2 = " MB)";
    private static final String CONNECT_BUTTON_LABEL = "Connect";
    private static final String TITLE_LABEL = "Start your analysis with EB solys";
    private static final String START_DEMO_BUTTON_LABEL = "Get Started with Demo File";
    private static final String EDIT_CONNECTION_BUTTON_TEXT = "...";
    private static final String UPGRADE_BUTTON_LABEL = "Upgrade to EB solys PRO";
    private static final String MODAL_WINDOW_MESSAGE = "Connect to a target, load a file or explore the tool in demo mode";

    private SolysButton connectButton;
    private SolysButton loadFileButton;
    private SolysButton startDemoButton;
    private SolysButton editConnectionButton;
    private SolysButton upgradeButton;
    private DropdownForConnections dropDown;

    private boolean connecting = false;
    private final Shell currentshell;

    private OpenFileInteractionUseCase loadFileInteractionUseCase = null;
    private ConnectionToTargetInteractionUseCase connectionToTargetInteractionUseCase = null;
    private ConnectionsNotifyUseCase connectionsNotifyUseCase = null;

    public ConnectToTargetModalWindow(Shell parentShell)
    {
        super( parentShell );
        currentshell = new Shell( parentShell );

        loadFileInteractionUseCase = UseCaseFactoryInstance.get()
                .makeLoadFileInteractionUseCase( new OpenFileInteractionHandler() );
        connectionToTargetInteractionUseCase = UseCaseFactoryInstance.get()
                .makeConnectionToTargetInteractionUseCase( new ConnectionToTargetInteractionHandler( this ) );
        connectionsNotifyUseCase = UseCaseFactoryInstance.get()
                .makeConnectionsNotifyUseCase( new ConnectionsNotifyHandler( this ) );
    }

    @Override
    protected Control createButtonBar(Composite parent)
    {
        Composite emptyComposite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        emptyComposite.setLayout( layout );
        emptyComposite.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );
        return emptyComposite;
    }

    @Override
    protected void buttonPressed(int buttonId)
    {
        switch (buttonId)
        {
            case CONNECT_BUTTON_ID :
                connectPressed();
                break;
            case OPEN_FILE_BUTTON_ID :
                openFilePressed();
                break;
            case EDIT_CONNECTION_BUTTON_ID :
                editConnectionPressed();
                break;
            case START_DEMO_BUTTON_ID :
                startDemoPressed();
                break;
            case UPGRADE_BUTTON_ID :
                break;
        }
    }

    private void connectPressed()
    {
        ConnectionModel connectionModel = dropDown.getActiveConnection();
        connectionToTargetInteractionUseCase.connect( connectionModel );

        setMessage( CONNECTING_MESSAGE_TEXT, IMessageProvider.NONE );
        connecting = true;
        enableButtons( false );
    }

    private void openFilePressed()
    {
        List<List<String>> filesTypesAndExtensions = loadFileInteractionUseCase.getAnotherFilesTypesAndExtensions();
        loadFile( filesTypesAndExtensions );
    }

    private void editConnectionPressed()
    {
        ConnectionModel connectionModel = dropDown.getActiveConnection();
        new EditHandler().editConnection( connectionModel );
    }

    private void startDemoPressed()
    {
        close();
        StartDemoHandler startDemoHandler = new StartDemoHandler();
        startDemoHandler.start();
    }

    @Override
    public void create()
    {
        super.create();
        connectButton.setEnabled( dropDown.getActiveConnection() != null );

        packWindowHeight();
        setLocation( currentshell );
        setTitle( TITLE_LABEL );
        setMessage( MODAL_WINDOW_MESSAGE, IMessageProvider.NONE );
    }

    private void packWindowHeight()
    {
        getShell().pack();
        int newHeigth = getShell().getSize().y;
        getShell().setSize( WINDOW_WIDTH, newHeigth );
    }

    private void setLocation(Shell shell)
    {
        Monitor primary = shell.getMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();

        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;

        shell.setLocation( x, y );
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite dialogArea = (Composite)super.createDialogArea( parent );

        Composite contents = new Composite( dialogArea, SWT.NONE );

        Composite connectionContainer = new Composite( contents, SWT.NONE );
        GridLayout layout = new GridLayout( 3, false );
        layout.verticalSpacing = 30;
        layout.horizontalSpacing = 5;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        connectionContainer.setLayout( layout );
        GridData connectionContainerGridData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        connectionContainer.setLayoutData( connectionContainerGridData );

        createConnectButton( connectionContainer );
        dropDown = new DropdownForConnections( connectionContainer );
        dropDown.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        createEditConnectionButton( connectionContainer );
        createMenuButtons( contents );

        UseCaseFactoryInstance.get().makeFileSizeLimitNotifyUseCase( this );

        return dialogArea;
    }

    private void createMenuButtons(Composite contents)
    {
        GridData contentsLayoutData = new GridData( SWT.FILL, SWT.CENTER, true, false );
        contents.setLayoutData( contentsLayoutData );
        GridLayout gridLayout = new GridLayout();
        contents.setLayout( gridLayout );

        loadFileButton = new SolysButton( contents, SWT.LEFT );
        loadFileButton.setText( OPEN_FILE_BUTTON_LABEL );
        Image fileIcon = ViewerCommonPlugin.getDefault().getImage( "file_solys", "png" );
        loadFileButton.setImage( fileIcon );
        GridData data = new GridData( SWT.FILL, SWT.CENTER, true, false );
        loadFileButton.setLayoutData( data );
        addSelectionListener( loadFileButton, OPEN_FILE_BUTTON_ID );

        startDemoButton = new SolysButton( contents, SWT.PUSH );
        startDemoButton.setText( START_DEMO_BUTTON_LABEL );
        Image demoIcon = ViewerCommonPlugin.getDefault().getImage( "start_demo_mode", "png" );
        startDemoButton.setImage( demoIcon );
        data = new GridData( SWT.FILL, SWT.CENTER, true, false );
        startDemoButton.setLayoutData( data );
        addSelectionListener( startDemoButton, START_DEMO_BUTTON_ID );

        if (!ProVersion.getInstance().isActive())
        {
            upgradeButton = new SolysButton( contents, SWT.PUSH );
            upgradeButton.setText( UPGRADE_BUTTON_LABEL );
            Image proUpgradeIcon = ViewerCommonPlugin.getDefault().getImage( "upgrade_to_pro_version", "png" );
            upgradeButton.setImage( proUpgradeIcon );
            data = new GridData( SWT.FILL, SWT.CENTER, true, false );
            upgradeButton.setLayoutData( data );
            addSelectionListener( upgradeButton, UPGRADE_BUTTON_ID );
        }

    }

    private void createConnectButton(Composite container)
    {
        connectButton = new SolysButton( container, SWT.RIGHT );
        connectButton.setText( CONNECT_BUTTON_LABEL );
        Image connectIcon = ViewerCommonPlugin.getDefault().getImage( "connection_disconnected", "png" );
        connectButton.setImage( connectIcon );
        GridData gridData = new GridData( SWT.CENTER, SWT.CENTER, false, false );
        connectButton.setLayoutData( gridData );
        addSelectionListener( connectButton, CONNECT_BUTTON_ID );
    }

    private void enableButtons(boolean enabled)
    {
        connectButton.setEnabled( enabled );
        loadFileButton.setEnabled( enabled );
        editConnectionButton.setEnabled( enabled );
        startDemoButton.setEnabled( enabled );
        if (upgradeButton != null)
        {
            upgradeButton.setEnabled( enabled );
        }
    }

    private void loadFile(List<List<String>> typesAndExtensions)
    {
        List<String> types = typesAndExtensions.get( 0 );
        List<String> extensions = typesAndExtensions.get( 1 );

        String[] typesAsArray = types.toArray( new String[types.size()] );
        String[] extensionsAsArray = extensions.toArray( new String[extensions.size()] );

        FileDialog fileDialog = new FileDialog( new Shell(), SWT.OPEN );
        fileDialog.setFilterNames( typesAsArray );
        fileDialog.setFilterExtensions( extensionsAsArray );
        String selectedFilePath = fileDialog.open();
        if (selectedFilePath != null)
        {
            loadFileInteractionUseCase.openFile( selectedFilePath );
            close();
        }
    }

    private void createEditConnectionButton(Composite parent)
    {
        editConnectionButton = new SolysButton( parent, SWT.PUSH );
        editConnectionButton.setText( EDIT_CONNECTION_BUTTON_TEXT );
        editConnectionButton.setLayoutData( new GridData( SWT.CENTER, SWT.CENTER, false, false ) );
        addSelectionListener( editConnectionButton, EDIT_CONNECTION_BUTTON_ID );
        editConnectionButton.setEnabled( dropDown.getActiveConnection() != null );
    }

    private void addSelectionListener(SolysButton button, int buttonId)
    {
        button.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                buttonPressed( buttonId );
            }
        } );
    }

    @Override
    public boolean close()
    {
        if (!connecting)
        {
            loadFileInteractionUseCase.unregister();
            connectionToTargetInteractionUseCase.unregister();
            connectionsNotifyUseCase.unregister();
            return super.close();
        }
        return false;
    }

    void setWindowToOriginalState()
    {
        setConnecting( false );
        enableButtons( true );
        setMessage( MODAL_WINDOW_MESSAGE, IMessageProvider.NONE );
    }

    void setConnecting(boolean connecting)
    {
        this.connecting = connecting;
    }

    void reloadConnections(List<ResourceModel> connections)
    {
        dropDown.setConnections( connections );
        boolean connectionAvailable = dropDown.getActiveConnection() != null;
        boolean buttonState = connectionAvailable && !connecting;
        connectButton.setEnabled( buttonState );
        editConnectionButton.setEnabled( buttonState );
    }

    @Override
    public void onFileSizeLimitChanged(long fileSizeLimitMB)
    {
        if (fileSizeLimitMB == Long.MAX_VALUE)
        {
            loadFileButton.setText( OPEN_FILE_BUTTON_LABEL );
        }
        else
        {
            loadFileButton.setText( OPEN_FILE_BUTTON_LABEL_WITH_LIMIT_PART1 + fileSizeLimitMB
                    + OPEN_FILE_BUTTON_LABEL_WITH_LIMIT_PART2 );
        }
    }
}
