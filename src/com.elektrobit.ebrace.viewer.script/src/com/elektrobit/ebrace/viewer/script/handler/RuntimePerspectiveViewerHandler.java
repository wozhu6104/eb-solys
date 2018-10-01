/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class RuntimePerspectiveViewerHandler extends AbstractHandler
{
    private static final String RUNTIME_PERSPECTIVE_ID = "com.elektrobit.ebrace.resourceconsumptionanalysis";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        try
        {
            PlatformUI.getWorkbench().showPerspective( RUNTIME_PERSPECTIVE_ID, activeWorkbenchWindow );
        }
        catch (WorkbenchException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
