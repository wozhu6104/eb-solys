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

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

public class TableModelImpl extends BaseResourceModel implements TableModel
{
    private boolean backgroundEnabled = true;

    public TableModelImpl(String name, ResourcesFolder parentFolder, ResourceChangedNotifier resourceChangedNotifier)
    {
        super( name, parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
    }

    @Override
    public void toggleBackgroundEnabled()
    {
        backgroundEnabled = !backgroundEnabled;
    }

    @Override
    public boolean isBackgroundEnabled()
    {
        return backgroundEnabled;
    }
}
