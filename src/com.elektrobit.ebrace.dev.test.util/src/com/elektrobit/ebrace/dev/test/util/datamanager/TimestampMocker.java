/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.datamanager;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

public class TimestampMocker
{
    public static Timestamp mock(long value)
    {
        return mock( value, value, value, value, value );
    }

    public static Timestamp mock(final long timeInMillis, final long relativeRaceTimeInMillies,
            final long absoluteRaceTimeInMillis, final long relativeTargetTimeInMillies,
            final long absoluteTargetTimeInMillies)
    {
        Timestamp timestampMock = new Timestamp()
        {

            @Override
            public long getTimeInMillis()
            {
                return timeInMillis;
            }

            @Override
            public long getRelativeTargetTimeInMillies()
            {
                return relativeTargetTimeInMillies;
            }

            @Override
            public long getRelativeRaceTimeInMillies()
            {
                return relativeRaceTimeInMillies;
            }

            @Override
            public long getAbsoluteTargetTimeInMillies()
            {
                return absoluteTargetTimeInMillies;
            }

            @Override
            public long getAbsoluteRaceTimeInMillis()
            {
                return absoluteRaceTimeInMillis;
            }
        };

        return timestampMock;
    }

    public static Timestamp mock(final long relativeRaceTimeInMillies, final long absoluteRaceTimeInMillis,
            final long relativeTargetTimeInMillies, final long absoluteTargetTimeInMillies)
    {
        return mock( 0,
                     relativeRaceTimeInMillies,
                     absoluteRaceTimeInMillis,
                     relativeTargetTimeInMillies,
                     absoluteTargetTimeInMillies );
    }
}
