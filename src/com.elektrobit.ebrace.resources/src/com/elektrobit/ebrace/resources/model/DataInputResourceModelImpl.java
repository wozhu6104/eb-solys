/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

public class DataInputResourceModelImpl extends BaseResourceModel implements DataInputResourceModel
{

    private boolean connected = false;

    public DataInputResourceModelImpl(String initialName, ResourcesFolder parent, EditRight editRight,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( initialName, parent, editRight, resourceChangedNotifier );
    }

    @Override
    public boolean isConnected()
    {
        return connected;
    }

    @Override
    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

}
