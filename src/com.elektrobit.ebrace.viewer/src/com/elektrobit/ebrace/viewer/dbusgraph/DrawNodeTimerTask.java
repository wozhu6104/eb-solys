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

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

class DrawNodeTimerTask extends TimerTask
{

    private final TreeNode node;
    private final GraphView parent;

    public DrawNodeTimerTask(GraphView parent, TreeNode node)
    {
        this.parent = parent;
        this.node = node;
    }

    @Override
    public void run()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                parent.update( node );
            }
        } );

    }
}
