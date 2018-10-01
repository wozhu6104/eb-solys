/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.reset;

import com.elektrobit.ebsolys.core.targetdata.api.adapter.Timestamp;

public class TimestampMock implements Timestamp
{

    private final int i;

    public TimestampMock(int i)
    {
        this.i = i;
    }

    @Override
    public long getRelativeRaceTimeInMillies()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getAbsoluteRaceTimeInMillis()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getRelativeTargetTimeInMillies()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getAbsoluteTargetTimeInMillies()
    {
        return System.currentTimeMillis() + i + 10;
    }

    @Override
    public long getTimeInMillis()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
