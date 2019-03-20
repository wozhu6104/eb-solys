/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.selectelement;

public interface StatusLineTextNotifyCallback
{
    public void onNewStatus(String status);

    public void onNewConnectionEstablished(String connectionName);

    public void onNewConnectionDataRate(String connectionName, float dataRate);

    public void onConnectionClosed(String connectionName);
}
