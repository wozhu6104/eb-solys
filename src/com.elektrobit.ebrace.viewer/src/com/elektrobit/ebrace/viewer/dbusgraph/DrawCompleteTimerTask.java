/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import java.util.TimerTask;

import org.eclipse.swt.widgets.Display;

class DrawCompleteTimerTask extends TimerTask
{
    private final GraphView parent;

    public DrawCompleteTimerTask(GraphView parent)
    {
        this.parent = parent;
    }

    @Override
    public void run()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                parent.drawComplete();
            }
        } );

    }

}
