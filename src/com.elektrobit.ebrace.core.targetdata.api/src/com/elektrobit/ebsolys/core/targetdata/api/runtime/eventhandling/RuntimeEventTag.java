/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling;

import lombok.Data;

/**
 * Represents tags (labels) of RuntimeEvents.
 * 
 * There are two predefined tags WARNING and ERROR. This class can be used to mark RuntimeEvent, e.g. in a table.
 */
@Data
public class RuntimeEventTag
{
    public static final RuntimeEventTag WARNING = new RuntimeEventTag( "WARNING" );
    public static final RuntimeEventTag ERROR = new RuntimeEventTag( "ERROR" );

    private final String tagName;
}
