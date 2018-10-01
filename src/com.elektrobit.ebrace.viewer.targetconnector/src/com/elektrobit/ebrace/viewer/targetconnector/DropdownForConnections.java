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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;

public class DropdownForConnections implements ConnectionStateNotifyCallback, SelectionListener, DisposeListener
{
    private static final String NEW_CONNECTION_HANDLER_ID = "com.elektrobit.ebrace.viewer.connection.newconnection";
    private static final String COMBOBOX_ADD_TEXT = "Add...";

    private CCombo connectionsComboBox;
    private final ConnectionStateNotifyUseCase targetConnectionNotifyUseCase;
    private final IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getService( IHandlerService.class );

    private List<ResourceModel> connections;
    private ConnectionModel activeConnection;

    public DropdownForConnections(Composite parent)
    {
        createDropDownMenu( parent );
        targetConnectionNotifyUseCase = UseCaseFactoryInstance.get().makeConnectionStateNotifyUseCase( this );

        connectionsComboBox.addDisposeListener( this );
        connectionsComboBox.addSelectionListener( this );
    }

    private void createDropDownMenu(Composite parent)
    {
        connectionsComboBox = new CCombo( parent, SWT.BORDER );
        connectionsComboBox.setEditable( false );
        connectionsComboBox.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
        connectionsComboBox.setVisibleItemCount( 4 );
    }

    public void setLayoutData(Object layoutData)
    {
        connectionsComboBox.setLayoutData( layoutData );
    }

    public ConnectionModel getActiveConnection()
    {
        return activeConnection;
    }

    @Override
    public void onTargetDisconnected()
    {
        enableDropDown( true );
    }

    @Override
    public void onTargetConnecting()
    {
        enableDropDown( false );
    }

    @Override
    public void onTargetConnected()
    {
        enableDropDown( false );
    }

    private void enableDropDown(final boolean state)
    {
        if (!connectionsComboBox.isDisposed())
        {
            connectionsComboBox.setEnabled( state );
        }
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        if (lastItemWasSelected())
        {
            callNewConnectionHandler();
        }
        else
        {
            setActiveConnection();
        }
    }

    private void setActiveConnection()
    {
        activeConnection = (ConnectionModel)connections.get( connectionsComboBox.getSelectionIndex() );
    }

    private void callNewConnectionHandler()
    {
        try
        {
            handlerService.executeCommand( NEW_CONNECTION_HANDLER_ID, null );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private boolean lastItemWasSelected()
    {
        return connectionsComboBox.getSelectionIndex() == (connectionsComboBox.getItemCount() - 1);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        targetConnectionNotifyUseCase.unregister();
    }

    public void setConnections(List<ResourceModel> connections)
    {
        this.connections = connections;
        connectionsComboBox.removeAll();

        for (ResourceModel resourceModel : connections)
        {
            ConnectionModel connectionModel = (ConnectionModel)resourceModel;
            connectionsComboBox.add( connectionModel.getName() );
        }

        connectionsComboBox.add( COMBOBOX_ADD_TEXT );

        if (connections.size() > 0)
        {
            connectionsComboBox.select( 0 );
            setActiveConnection();
        }
    }

}
