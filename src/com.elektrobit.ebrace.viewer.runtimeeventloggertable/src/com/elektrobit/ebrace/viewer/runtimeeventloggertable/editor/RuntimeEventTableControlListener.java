/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.editor;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;

public class RuntimeEventTableControlListener implements ControlListener
{

    private final RuntimeEventLoggerTableEditor parent;

    public RuntimeEventTableControlListener(RuntimeEventLoggerTableEditor parent)
    {
        this.parent = parent;
    }

    @Override
    public void controlResized(ControlEvent e)
    {
        parent.resizeColumns();
    }

    @Override
    public void controlMoved(ControlEvent e)
    {
    }

}
