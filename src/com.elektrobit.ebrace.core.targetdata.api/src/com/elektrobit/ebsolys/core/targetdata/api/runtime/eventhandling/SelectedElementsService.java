/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.listener.SelectedElementsChangedListener;

public interface SelectedElementsService
{
    public void setSelectedElements(List<TimebasedObject> tStamps);

    public List<TimebasedObject> getSelectedElements();

    void register(SelectedElementsChangedListener listener);

    void unregister(SelectedElementsChangedListener listener);

}
