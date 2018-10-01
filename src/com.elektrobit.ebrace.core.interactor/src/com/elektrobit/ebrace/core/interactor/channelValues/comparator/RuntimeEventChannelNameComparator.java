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

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelNameComparator implements Comparator<RuntimeEventChannel<?>>
{

    @Override
    public int compare(RuntimeEventChannel<?> arg0, RuntimeEventChannel<?> arg1)
    {
        String name1 = arg0.getName();
        String name2 = arg1.getName();

        int res = String.CASE_INSENSITIVE_ORDER.compare( name1, name2 );
        if (res == 0)
        {
            res = name1.compareTo( name2 );
        }
        return res;
    }

}
