/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.listener;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public interface SelectedElementsChangedListener
{
    void onNewTimeStamps(List<TimebasedObject> timeStamps);
}
