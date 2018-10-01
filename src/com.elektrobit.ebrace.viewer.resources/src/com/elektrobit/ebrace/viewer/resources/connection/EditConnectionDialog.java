/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

import lombok.Getter;

public class EditConnectionDialog extends Dialog implements ModifyListener, DisposeListener, SelectionListener
{
    private static final RGB RED = new RGB( 255, 0, 0 );
    private static final String PORT_LABEL = "Port";
    private static final String HOST_LABEL = "Host ";
    private static final String NAME_LABEL = "Name";
    private static final String TYPE_LABEL = "Type";
    private static final String EDIT_CONNECTION_TITLE = "Edit Connection";
    private static final String NEW_CONNECTION_TITLE = "New Connection";
    private static final String SAVE_TO_FILE_TOOLTIP = "Save incoming data into file when connected";
    private static final String SAVE_TO_FILE_LABEL = "Save incoming data to file";

    private Text nameText;
    private Text hostText;
    private Text portText;
    private Button saveToFileCheckbox;
    private Label verifyMessageLabel;
    private Color verifyMessageColor;

    private final ConnectionTypeSelector connectionTypeSelector;

    private final String nameToEdit;
    private final String hostToEdit;
    private final Integer portToEdit;
    private final Boolean saveToFileToEdit;
    private final Set<String> usedNames;
    private final List<ConnectionType> allConnectionTypes;
    private ConnectionType originalConnectionType;

    public EditConnectionDialog(String nameToEdit, String hostToEdit, Integer portToEdit, Boolean saveToFileToEdit,
            Shell parentShell, Set<String> usedNames, List<ConnectionType> allConnectionTypes,
            ConnectionType connectionType)
    {
        super( parentShell );
        this.nameToEdit = nameToEdit;
        this.hostToEdit = hostToEdit;
        this.portToEdit = portToEdit;
        this.saveToFileToEdit = saveToFileToEdit;
        this.usedNames = usedNames;
        this.allConnectionTypes = allConnectionTypes;
        this.originalConnectionType = connectionType;

        connectionTypeSelector = new ConnectionTypeSelector( allConnectionTypes, this );
        originalConnectionType = connectionType;
    }

    @Getter
    private String name, host;

    @Getter
    private int port;

    @Getter
    private ConnectionType selection;

    @Getter
    private boolean saveToFile;

    @Override
    protected Control createDialogArea(Composite parent)
    {
        Composite container = createContainer( parent );

        nameText = createField( container, NAME_LABEL, 3 );
        hostText = createField( container, HOST_LABEL, 1 );
        portText = createField( container, PORT_LABEL, 1 );
        List<String> connectionTypeLabels = new ArrayList<String>();
        for (ConnectionType connectionType : allConnectionTypes)
        {
            connectionTypeLabels.add( connectionType.getName() );
        }
        createRadioSelection( container, TYPE_LABEL, connectionTypeLabels, 2 );
        createCheckBox( container );
        createVerifyMessageLabel( container );

        setDefaultValues();
        registerTextChangedListeners();

        return dialogArea;
    }

    private Composite createContainer(Composite parent)
    {
        Composite dialogArea = (Composite)super.createDialogArea( parent );

        GridLayout layout = new GridLayout( 4, false );
        layout.marginLeft = 15;
        layout.marginTop = 15;
        layout.marginRight = 15;
        layout.marginBottom = 15;
        dialogArea.setLayout( layout );

        return dialogArea;
    }

    private Text createField(Composite parent, String fieldLabel, int horizontalSpan)
    {
        Label nameLabel = new Label( parent, SWT.NONE );
        nameLabel.setText( fieldLabel );

        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = horizontalSpan;

        Text text = new Text( parent, SWT.BORDER );
        text.setLayoutData( gridData );
        return text;
    }

    private void createRadioSelection(Composite parent, String fieldLabel, List<String> choices, int horizontalSpan)
    {
        GridData labelData = new GridData();
        labelData.verticalIndent = 5;
        labelData.verticalAlignment = GridData.BEGINNING;

        Label nameLabel = new Label( parent, SWT.NONE );
        nameLabel.setText( fieldLabel );
        nameLabel.setLayoutData( labelData );

        Composite selectionGroup = new Composite( parent, SWT.NONE );

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalSpan = horizontalSpan;

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;

        selectionGroup.setLayoutData( data );
        selectionGroup.setLayout( layout );

        connectionTypeSelector.initializeMap( selectionGroup );
        connectionTypeSelector.selectByType( originalConnectionType );
    }

    private void createCheckBox(Composite parent)
    {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 4;

        saveToFileCheckbox = new Button( parent, SWT.CHECK );
        saveToFileCheckbox.setSelection( true );
        saveToFileCheckbox.setText( SAVE_TO_FILE_LABEL );
        saveToFileCheckbox.setToolTipText( SAVE_TO_FILE_TOOLTIP );
        saveToFileCheckbox.setEnabled( true );
        saveToFileCheckbox.setLayoutData( gridData );
    }

    private void createVerifyMessageLabel(Composite parent)
    {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = 4;

        verifyMessageLabel = new Label( parent, SWT.NONE );
        verifyMessageLabel.setForeground( verifyMessageColor );
        verifyMessageLabel.setLayoutData( gridData );
    }

    @Override
    public void create()
    {
        super.create();
        validateInput();
    }

    @Override
    protected void configureShell(Shell newShell)
    {
        super.configureShell( newShell );
        setWindowSize( newShell );
        setTitle( newShell );
        newShell.addDisposeListener( this );

        verifyMessageColor = new Color( newShell.getDisplay(), RED );
    }

    private void setWindowSize(Shell shell)
    {
        shell.setMinimumSize( 350, 200 );
    }

    private void setTitle(Shell shell)
    {
        if (hostToEdit == null)
        {
            shell.setText( NEW_CONNECTION_TITLE );
        }
        else
        {
            shell.setText( EDIT_CONNECTION_TITLE );
        }
    }

    private void setDefaultValues()
    {
        if (nameToEdit != null)
        {
            nameText.setText( nameToEdit );
        }
        if (hostToEdit != null)
        {
            hostText.setText( hostToEdit );
        }

        if (portToEdit != null)
        {
            portText.setText( String.valueOf( portToEdit ) );
        }
        else
        {
            ConnectionType selected = connectionTypeSelector.selectDefault();
            int defaultPort = selected.getDefaultPort();
            portText.setText( String.valueOf( defaultPort ) );
        }

        if (saveToFileToEdit != null)
        {
            saveToFileCheckbox.setSelection( saveToFileToEdit );
        }
        if (originalConnectionType != null)
        {
            connectionTypeSelector.selectByType( originalConnectionType );
        }
    }

    private void registerTextChangedListeners()
    {
        nameText.addModifyListener( this );
        hostText.addModifyListener( this );
        portText.addModifyListener( this );
    }

    @Override
    protected boolean isResizable()
    {
        return true;
    }

    // save content of the Text fields because they get disposed
    // as soon as the Dialog closes
    private void saveInput()
    {
        name = nameText.getText();
        host = hostText.getText();
        port = Integer.parseInt( portText.getText() );
        selection = connectionTypeSelector.selectedType();
        saveToFile = saveToFileCheckbox.getSelection();
    }

    @Override
    protected void okPressed()
    {
        saveInput();
        super.okPressed();
    }

    @Override
    public void modifyText(ModifyEvent e)
    {
        validateInput();
    }

    private void validateInput()
    {
        Button okButton = getButton( IDialogConstants.OK_ID );
        if (okButton != null)
        {
            verifyMessageLabel.setText( "" );
            boolean inputValid = isInputValid();
            okButton.setEnabled( inputValid );
        }
    }

    private boolean isInputValid()
    {
        boolean nameValid = isNameValid();
        boolean hostValid = !hostText.getText().isEmpty();
        boolean portValid = !portText.getText().isEmpty() && isPortValid( portText.getText() );

        if (!portValid)
        {
            verifyMessageLabel.setText( "The port is not valid" );
        }

        boolean inputValid = nameValid && hostValid && portValid;
        return inputValid;
    }

    private boolean isNameValid()
    {
        String newName = nameText.getText();
        boolean nameUsed = usedNames.contains( newName );
        boolean nameNotEmpty = !newName.isEmpty();

        if (nameUsed)
        {
            verifyMessageLabel.setText( "The name is already used" );
        }

        return !nameUsed && nameNotEmpty;
    }

    private boolean isPortValid(String portText)
    {
        boolean isTextPresent = portText != null && !portText.isEmpty();
        if (!isTextPresent)
        {
            return false;
        }
        try
        {
            int port = Integer.valueOf( portText );
            if (port > 0 && port < 65535)
            {
                return true;
            }
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return false;
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        verifyMessageColor.dispose();
    }

    public ConnectionType getConnectionType()
    {
        return selection;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        ConnectionType selectedConnectionType = connectionTypeSelector.selectedType();
        int defaultPort = selectedConnectionType.getDefaultPort();
        portText.setText( String.valueOf( defaultPort ) );
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

}
