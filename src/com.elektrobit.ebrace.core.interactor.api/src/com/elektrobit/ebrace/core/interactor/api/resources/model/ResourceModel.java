/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.resources.model;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public interface ResourceModel extends ResourceTreeNode
{
    public List<RuntimeEventChannel<?>> getChannels();

    public void setChannels(List<RuntimeEventChannel<?>> channels);

    public void setDisabledChannels(List<RuntimeEventChannel<?>> disabledChannels);

    public List<RuntimeEventChannel<?>> getDisabledChannels();

    public List<RuntimeEventChannel<?>> getEnabledChannels();

    public void setSelectedChannels(List<RuntimeEventChannel<?>> selectedChannels);

    public List<RuntimeEventChannel<?>> getSelectedChannels();
}
