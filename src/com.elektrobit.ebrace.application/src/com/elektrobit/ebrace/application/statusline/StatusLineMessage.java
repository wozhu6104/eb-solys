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

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;

import com.elektrobit.ebrace.core.interactor.api.selectelement.StatusLineTextNotifyCallback;

public class StatusLineMessage implements StatusLineTextNotifyCallback
{

    private static final String CONNECTION_INFO = "connection.info";
    private static final String USER_INFO = "user.info";
    private final StatusLineContributionItem connectionInfoItem;
    private final StatusLineContributionItem userInfoItem;
    private final IStatusLineManager statusLineManager;

    public StatusLineMessage(IStatusLineManager statusLineManager)
    {
        this.statusLineManager = statusLineManager;
        connectionInfoItem = new StatusLineContributionItem( CONNECTION_INFO );
        connectionInfoItem.setText( "" );

        userInfoItem = new StatusLineContributionItem( USER_INFO );
        userInfoItem.setText( "" );

    }

    @Override
    public void onNewStatus(String status)
    {
        if (statusLineManager.find( USER_INFO ) == null)
        {
            if (statusLineManager.find( CONNECTION_INFO ) != null)
            {
                statusLineManager.insertBefore( CONNECTION_INFO, userInfoItem );
            }
            else
            {
                statusLineManager.add( userInfoItem );
            }
        }

        userInfoItem.setText( status );
    }

    @Override
    public void onNewConnectionInfo(String status)
    {

        if (statusLineManager.find( CONNECTION_INFO ) == null)
        {
            if (statusLineManager.find( USER_INFO ) != null)
            {
                statusLineManager.insertAfter( USER_INFO, connectionInfoItem );
            }
            else
            {
                statusLineManager.add( connectionInfoItem );
            }
        }

        connectionInfoItem.setText( status );
    }

}
