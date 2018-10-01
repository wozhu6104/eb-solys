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
import java.util.Map.Entry;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class DecodedRuntimeEventValueEntryComparator
        implements
            Comparator<Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent>>
{
    private final DecodedRuntimeEventNullComparator nullComparator = new DecodedRuntimeEventNullComparator();
    private final DecodedRuntimeEventNumberComparator numberComparator = new DecodedRuntimeEventNumberComparator();
    private final DecodedRuntimeEventBooleanComparator booleanComparator = new DecodedRuntimeEventBooleanComparator();

    @Override
    public int compare(Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent> entry1,
            Entry<RuntimeEventChannel<?>, DecodedRuntimeEvent> entry2)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "entry1", entry1 );
        RangeCheckUtils.assertReferenceParameterNotNull( "entry2", entry2 );
        DecodedRuntimeEvent event1 = entry1.getValue();
        DecodedRuntimeEvent event2 = entry2.getValue();

        return compareEvents( event1, event2 );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public int compareEvents(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        Comparator comparator = determineComparator( event1, event2 );
        return comparator.compare( event1, event2 );
    }

    private Comparator<?> determineComparator(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        if (isNull( event1, event2 ))
        {
            return nullComparator;
        }
        if (isNumber( event1 ))
        {
            return numberComparator;
        }
        if (isBoolean( event1 ))
        {
            return booleanComparator;
        }

        throw new UnsupportedOperationException( "Unsupported type" + event1.getRuntimeEventValue().getClass() );
    }

    private boolean isNull(DecodedRuntimeEvent event1, DecodedRuntimeEvent event2)
    {
        return event1 == null || event2 == null;
    }

    private boolean isNumber(DecodedRuntimeEvent event)
    {
        Class<?> eventType = event.getRuntimeEventValue().getClass();
        boolean result = Number.class.isAssignableFrom( eventType );
        return result;
    }

    private boolean isBoolean(DecodedRuntimeEvent event)
    {
        Class<?> eventType = event.getRuntimeEventValue().getClass();
        boolean result = Boolean.class.isAssignableFrom( eventType );
        return result;
    }

}
