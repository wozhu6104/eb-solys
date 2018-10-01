/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.api.manager;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public interface ResourceTreeChangedListener
{
    /**
     * Notifies that there was a change in the resource tree (e.g. deleted/renamed/moved resource or folder)
     */
    public void onResourceTreeChanged();

    public void onResourceDeleted(ResourceModel resourceModel);

    public void onResourceRenamed(ResourceModel resourceModel);

    /**
     * Notifies that a new resource is revealed
     */
    public void onResourceAdded(ResourceModel resourceModel);

    public void onOpenResourceModel(ResourceModel resourceModel);

}
