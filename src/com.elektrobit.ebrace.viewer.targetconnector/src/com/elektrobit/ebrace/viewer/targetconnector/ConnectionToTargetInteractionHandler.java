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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionToTargetInteractionCallback;

public class ConnectionToTargetInteractionHandler implements ConnectionToTargetInteractionCallback
{

    private final ConnectToTargetModalWindow connectToTargetModalWindow;

    public ConnectionToTargetInteractionHandler(ConnectToTargetModalWindow connectToTargetModalWindow)
    {
        this.connectToTargetModalWindow = connectToTargetModalWindow;
    }

    @Override
    public void onConnected()
    {
        connectToTargetModalWindow.setConnecting( false );
        connectToTargetModalWindow.close();
    }

    @Override
    public void onTargetNotReachable()
    {
        MessageDialog.open( SWT.ERROR,
                            connectToTargetModalWindow.getShell(),
                            LABEL_CONNECTION_NOT_POSSIBLE_TITLE,
                            LABEL_HOST_NOT_REACHABLE_TEXT,
                            SWT.NONE );
        connectToTargetModalWindow.setWindowToOriginalState();
    }

    @Override
    public void onDisconnected()
    {
    }
}
