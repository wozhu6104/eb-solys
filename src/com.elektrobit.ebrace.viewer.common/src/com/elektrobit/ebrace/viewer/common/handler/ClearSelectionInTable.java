/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;

public class ClearSelectionInTable extends AbstractHandler
{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchPart wPart = HandlerUtil.getActivePart( event );
        if (wPart instanceof ITableViewerView)
        {
            ITableViewerView tableViewerView = (ITableViewerView)wPart;
            tableViewerView.getTreeViewer().setSelection( StructuredSelection.EMPTY );
        }
        return null;
    }

}
