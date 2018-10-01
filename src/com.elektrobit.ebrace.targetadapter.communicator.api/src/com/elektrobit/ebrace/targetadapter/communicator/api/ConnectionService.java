/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetadapter.communicator.api;

import java.util.Set;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

public interface ConnectionService
{
    public boolean connect(ConnectionModel connectionModel);

    public void disconnect(ConnectionModel connectionModel);

    public Set<ConnectionType> getAllConnectionTypes();

    public Set<ConnectionModel> getAllActiveConnections();

    public void disconnectFromAllTargets();

    public void addConnectionStatusListener(ConnectionStatusListener connectionStatusListener);

    public void removeConnectionStatusListener(ConnectionStatusListener connectionStatusListener);

    public boolean everConnected();
}
