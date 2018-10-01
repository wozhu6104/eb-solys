/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.validation.handler;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;

public class RuntimeEventLogRecord extends LogRecord
{
    /**
     * 
     */
    private static final long serialVersionUID = -5291261623083952089L;
    RuntimeEvent<?> event;

    public RuntimeEventLogRecord(Level level, String message, RuntimeEvent<?> event)
    {
        super( level, message );
        this.event = event;
    }

    public RuntimeEvent<?> getEvent()
    {
        return event;
    }
}
