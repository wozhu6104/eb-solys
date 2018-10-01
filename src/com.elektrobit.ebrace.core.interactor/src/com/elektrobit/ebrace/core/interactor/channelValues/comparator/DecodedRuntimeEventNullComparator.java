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

public class DecodedRuntimeEventNullComparator implements Comparator<DecodedRuntimeEvent>
{

    private static final int ARGUMENT1_SMALLER_THAN_ARGUMENT2 = -1;
    private static final int ARGUMENT1_GREATER_THAN_ARGUMENT2 = 1;
    private static final int EQUALS = 0;

    @Override
    public int compare(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        if (areBothEventsNull( event1, event2 ))
        {
            return EQUALS;
        }

        if (isOnlyFirstEventNull( event1, event2 ))
        {
            return ARGUMENT1_GREATER_THAN_ARGUMENT2;
        }

        return ARGUMENT1_SMALLER_THAN_ARGUMENT2;
    }

    private boolean isOnlyFirstEventNull(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        return event1 != null && event2 == null;
    }

    private boolean areBothEventsNull(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        return event1 == null && event2 == null;
    }

}
