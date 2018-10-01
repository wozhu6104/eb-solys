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

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public class ConnectionsNotifyHandler implements ConnectionsNotifyCallback
{

    private final ConnectToTargetModalWindow connectToTargetModalWindow;

    public ConnectionsNotifyHandler(ConnectToTargetModalWindow connectToTargetModalWindow)
    {
        this.connectToTargetModalWindow = connectToTargetModalWindow;
    }

    @Override
    public void onConnectionsChanged(List<ResourceModel> connections)
    {
        connectToTargetModalWindow.reloadConnections( connections );
    }

}
