/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.channelValues.comparator;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.RuntimeEventType;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DecodedRuntimeEventMock implements DecodedRuntimeEvent
{

    private final Object value;

    public DecodedRuntimeEventMock()
    {
        this( null );
    }

    public DecodedRuntimeEventMock(Object value)
    {
        this.value = value;
    }

    @Override
    public DecodedTree getDecodedTree()
    {
        return null;
    }

    @Override
    public String getSummary()
    {
        return null;
    }

    @Override
    public RuntimeEventType getRuntimeEventType()
    {
        return null;
    }

    @Override
    public RuntimeEventChannel<?> getRuntimeEventChannel()
    {
        return null;
    }

    @Override
    public Object getRuntimeEventValue()
    {
        return value;
    }

    @Override
    public RuntimeEvent<?> getRuntimeEvent()
    {
        return null;
    }

}
