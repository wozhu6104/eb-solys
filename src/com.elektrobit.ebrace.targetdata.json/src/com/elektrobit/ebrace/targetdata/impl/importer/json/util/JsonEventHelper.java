/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.impl.importer.json.util;

import com.elektrobit.ebrace.core.targetdata.api.json.JsonEvent;
import com.elektrobit.ebrace.core.targetdata.api.json.JsonEventNew;

public class JsonEventHelper
{

    public static JsonEvent transformNew2OldEvent(JsonEventNew newEvent)
    {
        return new JsonEvent( newEvent.getUptime(),
                              newEvent.getChannel().getName(),
                              newEvent.getValue(),
                              newEvent.getDuration(),
                              newEvent.getEdge() );
    }

}
