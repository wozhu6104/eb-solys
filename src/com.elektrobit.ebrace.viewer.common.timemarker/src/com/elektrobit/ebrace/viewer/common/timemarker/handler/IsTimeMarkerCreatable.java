/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.handler;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;

public class IsTimeMarkerCreatable extends PropertyTester
{
    private static final String PROPERTY_NAME = "isTimeMarkerCreatable";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        boolean isTimeMarkerCreatable = false;
        if (PROPERTY_NAME.equals( property ))
        {
            IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (workbenchWindow != null && workbenchWindow.getActivePage() != null)
            {
                IWorkbenchPart part = workbenchWindow.getActivePage().getActivePart();
                if (part instanceof ITableViewerView)
                {
                    isTimeMarkerCreatable = !((ITableViewerView)part).getTreeViewer().getSelection().isEmpty();
                }
            }
        }
        return isTimeMarkerCreatable;
    }

}
