/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.connect;

public interface ConnectionToTargetInteractionCallback
{
    static final String LABEL_CONNECTION_NOT_POSSIBLE_TITLE = "Connection not possible";
    static final String LABEL_HOST_NOT_REACHABLE_TEXT = "Connection could not be established. Verify that host is reachable and Target Agent is running.";
    static final String LABEL_EMPTY_IP_PORT = "IP address or Port is empty, cannot connect!";

    public void onConnected();

    public void onTargetNotReachable();

    public void onDisconnected();
}
