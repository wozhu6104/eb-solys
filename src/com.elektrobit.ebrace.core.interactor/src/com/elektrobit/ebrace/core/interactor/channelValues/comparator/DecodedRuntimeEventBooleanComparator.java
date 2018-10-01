/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.channelValues.comparator;

import java.util.Comparator;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;

public class DecodedRuntimeEventBooleanComparator implements Comparator<DecodedRuntimeEvent>
{

    @Override
    public int compare(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        Boolean event1BooleanValue = (Boolean)event1.getRuntimeEventValue();
        Boolean event2BooleanValue = (Boolean)event2.getRuntimeEventValue();

        return event1BooleanValue.compareTo( event2BooleanValue );
    }

}
