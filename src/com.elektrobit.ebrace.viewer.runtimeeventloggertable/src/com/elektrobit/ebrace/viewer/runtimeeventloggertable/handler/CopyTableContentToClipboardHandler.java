/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableSelectionToClipboardHelper;

public class CopyTableContentToClipboardHandler extends AbstractHandler implements IHandler
{

    private final TableSelectionToClipboardHelper clipboard;

    public CopyTableContentToClipboardHandler()
    {
        super();
        clipboard = new TableSelectionToClipboardHelper( Display.getCurrent() );
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        ISelection selectedTableContent = HandlerUtil.getCurrentSelectionChecked( event );
        if (selectedTableContent instanceof IStructuredSelection)
        {
            clipboard.copy( (IStructuredSelection)selectedTableContent );
        }
        return null;
    }

    @Override
    public void dispose()
    {
        clipboard.dispose();
        super.dispose();
    }
}
