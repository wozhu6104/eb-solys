/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import java.util.List;

import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.script.external.SSnapshot;

public class SSnapshotImpl extends SBaseResourceImpl<SnapshotModel, SSnapshot> implements SSnapshot
{
    public SSnapshotImpl(SnapshotModel viewModel, ResourcesModelManager resourcesModelManager)
    {
        super( viewModel, resourcesModelManager );
    }

    @Override
    protected SSnapshot getThis()
    {
        return this;
    }

    @Override
    protected boolean canChannelsBeAddedToView(List<RuntimeEventChannel<?>> channels)
    {
        return true;
    }
}
