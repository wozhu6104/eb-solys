/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.checker;

import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;

public class TimeMarkerPrefixChecker extends AbstractChecker<TimeMarker, String>
{
    public TimeMarkerPrefixChecker(String prefix)
    {
        super( prefix );
    }

    @Override
    public boolean validate(TimeMarker timeMarker)
    {
        return timeMarker.getName().startsWith( validator );
    }

}
