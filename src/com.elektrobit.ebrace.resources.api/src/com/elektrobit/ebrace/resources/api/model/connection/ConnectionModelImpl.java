/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.api.model.connection;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

import lombok.Getter;
import lombok.Setter;

public class ConnectionModelImpl extends BaseResourceModel implements ConnectionModel
{
    @Getter
    private String host;

    @Getter
    private int port;

    @Getter
    private boolean saveToFile = false;

    @Getter
    private String recordingsFolder = "recordings";

    @Getter
    private String recordingFilePrefix = "eb-solys";

    @Getter
    private boolean connected = false;

    @Getter
    @Setter
    private ConnectionType connectionType;

    public ConnectionModelImpl(String connectionName, String host, int port, boolean saveToFile,
            ConnectionType connectionType, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( connectionName, parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
        this.host = host;
        this.port = port;
        this.saveToFile = saveToFile;
        this.connectionType = connectionType;
    }

    public ConnectionModelImpl(ConnectionSettings connectionSettings, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        this( connectionSettings.getName(),
                connectionSettings.getHost(),
                connectionSettings.getPort(),
                connectionSettings.isSaveToFile(),
                connectionSettings.getConnectionType(),
                parentFolder,
                resourceChangedNotifier );
    }

    @Override
    public void setHost(String host)
    {
        this.host = host;
        notifyResourceStateChanged();
    }

    @Override
    public void setPort(int port)
    {
        this.port = port;
        notifyResourceStateChanged();
    }

    @Override
    public void setSaveToFile(boolean saveToFile)
    {
        this.saveToFile = saveToFile;
        notifyResourceStateChanged();
    }

    @Override
    public void setRecordingsFolder(String recordingsFolder)
    {
        this.recordingsFolder = recordingsFolder;
        notifyResourceStateChanged();
    }

    @Override
    public void setRecordingsFilePrefix(String recordingsFilePrefix)
    {
        this.recordingFilePrefix = recordingsFilePrefix;
        notifyResourceStateChanged();
    }

    @Override
    public void setConnected(boolean connected)
    {
        this.connected = connected;
        notifyResourceStateChanged();
    }

    @Override
    public EditRight getEditRight()
    {
        return connected ? EditRight.READ_ONLY : EditRight.EDITABLE;
    }

    @Override
    public ConnectionSettings toConnectionSettings()
    {
        return new ConnectionSettings( getName(), getHost(), getPort(), isSaveToFile(), getConnectionType() );
    }

    @Override
    public String getId()
    {
        return getConnectionType().getExtension() + "@" + host.replace( '.', '_' ) + ":" + port + ".";
    }

}
