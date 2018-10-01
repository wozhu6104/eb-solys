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

public interface ResourceChangedListener
{
    /**
     * Notifies that channels in single resource model have changed.
     * 
     * @param resourceModel
     */
    public void onResourceModelChannelsChanged(ResourceModel resourceModel);

    /**
     * Notifies that selected channels in single resource model have changed.
     * 
     * @param resourceModel
     */
    public void onResourceModelSelectedChannelsChanged(ResourceModel resourceModel);
}
