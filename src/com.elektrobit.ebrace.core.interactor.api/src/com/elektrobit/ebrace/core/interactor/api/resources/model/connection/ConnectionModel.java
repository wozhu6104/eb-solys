/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model.connection;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public interface ConnectionModel extends ResourceModel
{
    public String getHost();

    public void setHost(String host);

    public int getPort();

    public void setPort(int port);

    public String getId();

    public boolean isConnected();

    public boolean isSaveToFile();

    public String getRecordingsFolder();

    public String getRecordingFilePrefix();

    public void setConnected(boolean connected);

    public void setSaveToFile(boolean saveToFile);

    public void setRecordingsFolder(String recordingsFolder);

    public void setRecordingsFilePrefix(String recordingsFilePrefix);

    public void setConnectionType(ConnectionType connectionType);

    public ConnectionType getConnectionType();

    public ConnectionSettings toConnectionSettings();
}
