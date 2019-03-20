/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.statusline;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;

import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyCallback;

public class StatusLineMessage implements StatusLineTextNotifyCallback
{

    private static final String CONNECTION_INFO = "connection.info";
    private static final String USER_INFO = "user.info";
    private final StatusLineContributionItem userInfoItem;
    private final IStatusLineManager statusLineManager;

    public StatusLineMessage(IStatusLineManager statusLineManager)
    {
        this.statusLineManager = statusLineManager;

        userInfoItem = new StatusLineContributionItem( USER_INFO );
        userInfoItem.setText( "" );

    }

    @Override
    public void onNewStatus(String status)
    {
        if (statusLineManager.find( USER_INFO ) == null)
        {
            IContributionItem firstConnectionInfoItem = findFirstConnectionInfoItem();
            if (firstConnectionInfoItem != null)
            {
                statusLineManager.insertBefore( firstConnectionInfoItem.getId(), userInfoItem );
            }
            else
            {
                statusLineManager.add( userInfoItem );
            }
        }

        userInfoItem.setText( status );
    }

    private IContributionItem findFirstConnectionInfoItem()
    {
        Optional<IContributionItem> first = Arrays.asList( statusLineManager.getItems() ).stream()
                .filter( item -> item.getId().startsWith( CONNECTION_INFO ) ).findFirst();

        if (first.isPresent())
        {
            return first.get();
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onNewConnectionEstablished(String connectionName)
    {

        StatusLineContributionItem connectionInfoItem = findConnectionItem( CONNECTION_INFO + "." + connectionName );
        if (connectionInfoItem == null)
        {
            connectionInfoItem = new StatusLineContributionItem( CONNECTION_INFO + "." + connectionName );
            statusLineManager.add( connectionInfoItem );
        }

        connectionInfoItem.setText( connectionName + ": connected" );
    }

    @Override
    public void onNewConnectionDataRate(String connectionName, float dataRate)
    {
        StatusLineContributionItem connectionInfoItem = findConnectionItem( CONNECTION_INFO + "." + connectionName );
        connectionInfoItem.setText( connectionName + ": " + (dataRate * 8) / 1000 + " MBit/s" );
    }

    private StatusLineContributionItem findConnectionItem(String connectionName)
    {
        List<IContributionItem> asList = Arrays.asList( statusLineManager.getItems() );
        Optional<IContributionItem> first = asList.stream().filter( item -> item.getId().equals( connectionName ) )
                .findFirst();

        if (first.isPresent())
        {
            return (StatusLineContributionItem)first.get();
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onConnectionClosed(String connectionName)
    {
        statusLineManager.remove( CONNECTION_INFO + "." + connectionName );
    }

}
