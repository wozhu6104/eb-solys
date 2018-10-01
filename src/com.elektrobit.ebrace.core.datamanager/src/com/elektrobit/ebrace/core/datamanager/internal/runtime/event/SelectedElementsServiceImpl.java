/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.runtime.event;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;
import com.elektrobit.ebsolys.core.targetdata.api.listener.SelectedElementsChangedListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.SelectedElementsService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

@Component
public class SelectedElementsServiceImpl implements SelectedElementsService
{
    private List<TimebasedObject> timeStamps;
    private final GenericListenerCaller<SelectedElementsChangedListener> registeredListeners = new GenericListenerCaller<SelectedElementsChangedListener>();

    @Override
    public void setSelectedElements(List<TimebasedObject> tStamps)
    {
        timeStamps = tStamps;
        notifyListeners();
    }

    @Override
    public List<TimebasedObject> getSelectedElements()
    {
        return timeStamps;
    }

    @Override
    public void register(SelectedElementsChangedListener listener)
    {
        registeredListeners.add( listener );
    }

    @Override
    public void unregister(SelectedElementsChangedListener listener)
    {
        registeredListeners.remove( listener );
    }

    private void notifyListeners()
    {
        registeredListeners.notifyListeners( new Notifier<SelectedElementsChangedListener>()
        {

            @Override
            public void notify(SelectedElementsChangedListener listener)
            {
                listener.onNewTimeStamps( timeStamps );
            }
        } );
    }
}
